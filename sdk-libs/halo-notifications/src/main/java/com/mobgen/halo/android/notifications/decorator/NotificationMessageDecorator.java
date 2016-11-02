package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

/**
 * @hide Notification decorator that sets the message from the bundle on the notification.
 */
public class NotificationMessageDecorator extends HaloNotificationDecorator {

    /**
     * Constructor for the message notification.
     *
     * @param decorator The decorator to chain the notification.
     */
    public NotificationMessageDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String message = bundle.getString("body");
        if (!TextUtils.isEmpty(message)) {
            builder.setContentText(message);
        }
        return chain(builder, bundle);
    }
}
