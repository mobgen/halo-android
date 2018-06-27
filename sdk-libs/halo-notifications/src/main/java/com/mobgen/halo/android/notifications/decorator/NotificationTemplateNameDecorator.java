package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

/**
 * @hide Notification decorator that sets the template name from the bundle on the notification.
 */
public class NotificationTemplateNameDecorator extends HaloNotificationDecorator {
    /**
     * Constructor for the message notification.
     *
     * @param decorator The decorator to chain the notification.
     */
    public NotificationTemplateNameDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String template = bundle.getString("template");
        if (!TextUtils.isEmpty(template)) {
            builder.setContentText(template);
        }

        return chain(builder, bundle);
    }
}