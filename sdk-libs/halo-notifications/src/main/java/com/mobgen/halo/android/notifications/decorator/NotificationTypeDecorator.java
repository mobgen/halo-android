package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

/**
 * @hide Notification decorator that sets the type from the bundle on the notification.
 */
public class NotificationTypeDecorator extends HaloNotificationDecorator {
    /**
     * Constructor for the message notification.
     *
     * @param decorator The decorator to chain the notification.
     */
    public NotificationTypeDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String type = bundle.getString("type");
        if (!TextUtils.isEmpty(type)) {
            builder.setContentText(type);
        }

        return chain(builder, bundle);
    }
}