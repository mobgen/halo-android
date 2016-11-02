package com.mobgen.halo.android.notifications.decorator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

/**
 * @hide Decorator that adds the possibility to process the click_action element from the notification
 * to open an activity when the notification is clicked.
 */
public class NotificationActionDecorator extends HaloNotificationDecorator {

    /**
     * The default intent value.
     */
    private static final String DEFAULT_INTENT = "com.mobgen.halo.android.sdk.notifications.OPEN";

    /**
     * The context used to create a pending intent.
     */
    private Context mContext;

    /**
     * The action intent.
     */
    private Intent mActionIntent;

    /**
     * Constructor for the action decorator.
     *
     * @param context   The context for the notification service.
     * @param decorator The notification decorator.
     */
    public NotificationActionDecorator(Context context, HaloNotificationDecorator decorator) {
        super(decorator);
        mContext = context;
        mActionIntent = new Intent();
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String action = bundle.getString("click_action");
        if (!TextUtils.isEmpty(action)) {
            mActionIntent.setAction(action);
        } else {
            mActionIntent.setAction(DEFAULT_INTENT);
        }
        mActionIntent.putExtras(bundle);
        mActionIntent.setPackage(mContext.getPackageName());
        mActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0, mActionIntent, Intent.FILL_IN_PACKAGE | PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setAutoCancel(true);
        return chain(builder, bundle);
    }

    /**
     * Sets the intent for testing purposes.
     *
     * @param intent The intent.
     */
    @VisibleForTesting
    public void setIntent(Intent intent) {
        mActionIntent = intent;
    }
}
