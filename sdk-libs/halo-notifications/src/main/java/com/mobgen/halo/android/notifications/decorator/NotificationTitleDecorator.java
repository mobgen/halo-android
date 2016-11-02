package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

/**
 * @hide Title decorator that sets the title from the bundle.
 */
public class NotificationTitleDecorator extends HaloNotificationDecorator {

    /**
     * The constructor to provide another chained decorator.
     *
     * @param decorator The decorator.
     */
    public NotificationTitleDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String title = bundle.getString("title");
        if (!TextUtils.isEmpty(title)) {
            builder.setContentTitle(title);
        }
        return chain(builder, bundle);
    }
}
