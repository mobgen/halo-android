package com.mobgen.halo.android.notifications.callbacks;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;

/**
 * Created by f.souto.gonzalez on 23/01/2018.
 */

/**
 * Provides a callback to make some operation over notification
 */
@Keep
public interface HaloNotificationEventListener {

    /**
     * Callback to perform some operation over notifications.
     *
     * @param haloPushEvent The push notification event.
     */
    @Keep
    @Api(2.0)
    void onEventReceived(@Nullable HaloPushEvent haloPushEvent);
}
