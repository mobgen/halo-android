package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * @hide Notification decorator that sets the led functionality.
 */
public class NotificationLedDecorator extends HaloNotificationDecorator {

    /**
     * On time for the notification led decorator.
     */
    protected static final int ON_TIME = 1500;
    /**
     * Off time for the notification led decorator.
     */
    protected static final int OFF_TIME = 4000;

    /**
     * Constructor for the led decorator that chains to another decorator.
     *
     * @param decorator The decorator instance to chain.
     */
    public NotificationLedDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String color = bundle.getString("color");
        if (!TextUtils.isEmpty(color)) {
            builder.setLights(HaloUtils.getArgb(color), ON_TIME, OFF_TIME);
        }
        return chain(builder, bundle);
    }
}
