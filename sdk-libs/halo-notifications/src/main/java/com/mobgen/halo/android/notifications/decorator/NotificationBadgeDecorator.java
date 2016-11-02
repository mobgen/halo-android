package com.mobgen.halo.android.notifications.decorator;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

/**
 * @hide Adds the number parameter. This parameter can be used to perform some additional actions on the notification
 * such as know how much notifications are present.
 */
public class NotificationBadgeDecorator extends HaloNotificationDecorator {

    /**
     * The notification badge constructor.
     *
     * @param decorator The decorator on which the chain will be produced.
     */
    public NotificationBadgeDecorator(HaloNotificationDecorator decorator) {
        super(decorator);
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String badge = bundle.getString("badge");
        if (!TextUtils.isEmpty(badge)) {
            try {
                int badgeNumber = Integer.parseInt(badge);
                builder.setNumber(badgeNumber);
            } catch (NumberFormatException e) {
                Halog.e(getClass(), "The badge field on the notification is not a number");
            }
        }
        return chain(builder, bundle);
    }
}
