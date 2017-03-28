package com.mobgen.halo.android.notifications.services;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Created by fernandosouto on 28/03/17.
 */

/**
 * Custom notification id generation.
 *
 */
@Keep
public interface CustomIdGeneration {


    /**
     * Get the next id for notification.
     *
     *
     * @param data The bundle data of the notification.
     * @return The notification id.
     */
    @Keep
    @Api(2.3)
    int getNextNotificationId(@NonNull Bundle data, int currentId);

}
