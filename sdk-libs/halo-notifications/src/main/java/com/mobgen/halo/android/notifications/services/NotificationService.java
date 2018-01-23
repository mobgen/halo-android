package com.mobgen.halo.android.notifications.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationActionDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationBadgeDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationColorDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationIconDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationImageDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationLedDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationMessageDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationSoundDecorator;
import com.mobgen.halo.android.notifications.decorator.NotificationTitleDecorator;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.sdk.api.Halo;

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
     * The custom id generator.
     */
    @NonNull
    private static NotificationIdGenerator mIdGenerator;

    /**
     * The two factor key
     */
    private static final String TWO_FACTOR_CODE = "2_FACTOR";

    /**
     * The silent notification key
     */
    private static final String SILENT_KEY = "1";

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
     * True if the action event api is enable; Otherwise false.
     */
    private static boolean mActionEvents = false;

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
            if (!key.equals("extra")) {
                bundle.putString(key, data.get(key));
            } else {
                if (data.get(key) != null) {
                    bundle.putString(key, data.get(key));
                    extraJSONToBundle(bundle, data.get(key));
                }
            }
        }
        return bundle;
    }

    /**
     * Parse extra json to bundle.
     *
     * @param bundle    Bundle
     * @param extraData String  with json extra data
     * @throws JSONException
     */
    private void extraJSONToBundle(@NonNull Bundle bundle, @NonNull String extraData) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(extraData);
            if (jsonObject != null) {
                Iterator iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = jsonObject.getString(key);
                    bundle.putString(key, value);
                }
            }
        } catch (JSONException jsonException) {
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Bundle dataBundle = messageToBundle(message);

        //notify that push was received correctly
        if (mActionEvents && Halo.instance().manager().getDevice() != null) {
            Intent receiptIntent = new Intent();
            receiptIntent.setAction(HaloUtils.getEventName(Halo.instance().context(), NotificationEmitter.NOTIFICATION_EVENT) + NotificationEventsActions.PUSH_RECEIPT);
            receiptIntent.putExtra("action", NotificationEventsActions.PUSH_RECEIPT);
            receiptIntent.putExtra("scheduleId", dataBundle.getString("scheduleId"));
            NotificationEmitter.emitNotificationEventAction(this, receiptIntent);
        }

        String from = message.getFrom();
        if (isTwoFactor(dataBundle)) {
            //Let the two factor handle it
            NotificationEmitter.emitTwoFactor(this, from, dataBundle);
        } else if (isSilent(dataBundle)) {
            //Let the silent handle manage it
            NotificationEmitter.emitSilent(this, from, dataBundle);
        } else {
            //set the notification id
            int notificationId = mIdGenerator.getNextNotificationId(dataBundle, mNotificationId.getAndIncrement());
            //Build notification based on decorators
            NotificationCompat.Builder builder = createNotificationDecorator().decorate(new NotificationCompat.Builder(this), dataBundle);
            //Notify if available and the decorator provides a builder. If a custom decorator provides a null builder
            //We should not crash
            if (builder != null) {
                if (mActionEvents) {
                    Intent deleteIntent = new Intent();
                    deleteIntent.setAction(HaloUtils.getEventName(Halo.instance().context(), NotificationEmitter.NOTIFICATION_EVENT) + NotificationEventsActions.PUSH_DISMISS);
                    deleteIntent.putExtra("action", NotificationEventsActions.PUSH_DISMISS);
                    deleteIntent.putExtra("scheduleId", dataBundle.getString("scheduleId"));
                    builder.setDeleteIntent(PendingIntent.getBroadcast(this, 0,
                            deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                    Intent opentIntent = new Intent();
                    opentIntent.setAction(HaloUtils.getEventName(Halo.instance().context(), NotificationEmitter.NOTIFICATION_EVENT) + NotificationEventsActions.PUSH_OPEN);
                    deleteIntent.setAction(NotificationEventsActions.PUSH_OPEN);
                    opentIntent.putExtra("action", NotificationEventsActions.PUSH_OPEN);
                    opentIntent.putExtra("scheduleId", dataBundle.getString("scheduleId"));
                    builder.setContentIntent(PendingIntent.getBroadcast(this, 1,
                            opentIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //Just notify
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
        return SILENT_KEY.equalsIgnoreCase(data.getString("content_available"));
    }

    /**
     * A notification is considered Two Factor Authentiaction if comes with the 2_FACTOR code.
     *
     * @param data
     * @return True if it is a silent notification. False otherwise.
     */
    private boolean isTwoFactor(@NonNull Bundle data) {
        return TWO_FACTOR_CODE.equalsIgnoreCase(data.getString("type"));
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
                                                        new NotificationImageDecorator(this,
                                                                new NotificationMessageDecorator(
                                                                        new NotificationTitleDecorator(
                                                                                mDecorator
                                                                        )))))))));
    }

    /**
     * Set the notification id generator.
     *
     * @param idGenerator The id generator.
     */
    public static void setIdGenerator(@NonNull NotificationIdGenerator idGenerator) {
        AssertionUtils.notNull(idGenerator, "idGenerator");
        mIdGenerator = idGenerator;
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
     * Gets the custom decorator if its available.
     *
     * @return The custom decorator.
     */
    @Nullable
    public static HaloNotificationDecorator getNotificationDecorator() {
        return mDecorator;
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

    /**
     * Enable push action event notifications.
     */
    public static void enablePushEvents() {
        mActionEvents = true;
    }
}
