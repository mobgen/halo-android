package com.mobgen.halo.android.notifications.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationActionDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationBadgeDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationColorDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationIconDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationLedDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationMessageDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationSoundDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationTitleDecorator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @hide The Halo gcm service is a long running service that allows an application to receive push notifications and
 * react on them when they are clicked.
 */
@Keep
@SuppressLint("Registered")
public class NotificationService extends FirebaseMessagingService {

    /**
     * The atomic identifier to display every notification in a different view.
     */
    private static final AtomicInteger mNotificationId = new AtomicInteger(0);

    /**
     * The custom decorator that the user can set just to add some behaviour on the notifications. This decorator
     * will be set as a leaf, so there is no need to call to {@link HaloNotificationDecorator#chain(NotificationCompat.Builder, Bundle) chain} unless
     * you set other decorators dependant on this.
     */
    private static HaloNotificationDecorator mDecorator;

    /**
     * The notification id that is visible.
     */
    private static final String NOTIFICATION_ID = "halo_ui_notification_id";

    /**
     * Parse a RemoteMessage into a bundle
     *
     * @param message RemoteMessage
     * @return The Bundle
     */
    private Bundle messageToBundle(RemoteMessage message) {
        Map<String, String> data = message.getData();
        Bundle bundle = new Bundle();

        for (String key : data.keySet()) {
            if(!key.equals("extra")) {
                bundle.putString(key, data.get(key));
            } else{
                if(data.get(key)!=null) {
                    bundle.putString(key, data.get(key));
                    extraJSONToBundle(bundle, data.get(key));
                }
            }
        }
        return bundle;
    }

    /**
     *
     * Parse extra json to bundle.
     *
     * @param bundle Bundle
     * @param extraData String  with json extra data
     * @throws JSONException
     */
    private void extraJSONToBundle(@NonNull Bundle bundle , @NonNull String extraData) {
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(extraData);
            if(jsonObject!=null) {
                Iterator iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = jsonObject.getString(key);
                    bundle.putString(key, value);
                }
            }
        } catch (JSONException jsonException){
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String from = message.getFrom();

        Bundle dataBundle = messageToBundle(message);

        if (isSilent(dataBundle)) {
            //Let the silent handle manage it
            NotificationEmitter.emitSilent(this, from, dataBundle);
        } else if(isTwoFactor(dataBundle)) {
            //Let the two factor handle it
            NotificationEmitter.emitTwoFactor(this, from, dataBundle);
        } else {
            //Build notification based on decorators
            NotificationCompat.Builder builder = createNotificationDecorator().decorate(new NotificationCompat.Builder(this), dataBundle);
            //Notify if available and the decorator provides a builder. If a custom decorator provides a null builder
            //We should not crash
            if (builder != null) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //Just notify
                int notificationId = mNotificationId.getAndIncrement();
                notificationManager.notify(notificationId, builder.build());
                dataBundle.putString(NOTIFICATION_ID, String.valueOf(notificationId));
            }
            //Notify the handler
            NotificationEmitter.emitNotSilent(this, from, dataBundle);
        }
        //Decide what to do based on the notification behaviour
        NotificationEmitter.emitAll(this, from, dataBundle);
        Halog.d(getClass(), "Message received " + dataBundle.toString());
    }

    /**
     * A notification is considered silent when the body contains the string silent.
     *
     * @param data The data provided in the notification.
     * @return True if it is a silent notification. False otherwise.
     */
    private boolean isSilent(@NonNull Bundle data) {
        return "1".equalsIgnoreCase(data.getString("content_available"));
    }


    private boolean isTwoFactor(@NonNull Bundle data) {
        return "2_FACTOR".equalsIgnoreCase(data.getString("type"));
    }

    /**
     * Creates a the decorator with all the components provided for the notifications and available from the Halo service.
     *
     * @return The notification decorator.
     */
    @NonNull
    private HaloNotificationDecorator createNotificationDecorator() {
        return new NotificationActionDecorator(this,
                new NotificationIconDecorator(this,
                        new NotificationSoundDecorator(this,
                                new NotificationBadgeDecorator(
                                        new NotificationColorDecorator(
                                                new NotificationLedDecorator(
                                                        new NotificationMessageDecorator(
                                                                new NotificationTitleDecorator(
                                                                        mDecorator
                                                                ))))))));
    }

    /**
     * Sets the custom decorator to be used when a notification is available.
     *
     * @param decorator The custom decorator.
     */
    public static void setNotificationDecorator(@Nullable HaloNotificationDecorator decorator) {
        mDecorator = decorator;
    }

    /**
     * Provides the notification id for the provided bundle.
     *
     * @param bundle The data inside of the bundle.
     * @return The integer value of the notification, or null if this notification was never shown.
     */
    @Nullable
    public static Integer getNotificationId(@NonNull Bundle bundle) {
        Integer notificationId = null;
        if (bundle.containsKey(NOTIFICATION_ID)) {
            notificationId = bundle.getInt(NOTIFICATION_ID);
        }
        return notificationId;
    }
}
