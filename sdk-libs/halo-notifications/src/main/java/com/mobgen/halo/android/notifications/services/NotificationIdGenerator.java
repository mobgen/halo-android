package com.mobgen.halo.android.notifications.services;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Custom notification id generation to provide a custom notification id. Also you can modify the data bundle of the notification.
 *
 */
@Keep
public interface NotificationIdGenerator {


    /**
     * Get the next id for notification.
     *
     *
     * @param data The bundle data of the notification.
     * @param currentId The current notification id.
     * @return The notification id.
     */
    @Keep
    @Api(2.3)
    int getNextNotificationId(@NonNull Bundle data, int currentId);

}
