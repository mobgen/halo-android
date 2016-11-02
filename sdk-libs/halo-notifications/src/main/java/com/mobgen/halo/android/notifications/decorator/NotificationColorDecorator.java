package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * @hide Notification decorator for the color icon.
 */
public class NotificationColorDecorator extends HaloNotificationDecorator {

    /**
     * Constructor for the color decorator that chains to another decorator.
     *
     * @param decorator The decorator instance to chain.
     */
    public NotificationColorDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String color = bundle.getString("color");
        if (!TextUtils.isEmpty(color)) {
            builder.setColor(HaloUtils.getArgb(color));
        }
        return chain(builder, bundle);
    }
}
