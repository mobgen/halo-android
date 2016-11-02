package com.mobgen.halo.android.framework.toolbox.bus;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Interface for the subscribers of an event.
 */
public interface Subscriber {
    /**
     * Callback for the event received.
     *
     * @param event The event received.
     */
    @Api(1.3)
    void onEventReceived(@NonNull Event event);
}