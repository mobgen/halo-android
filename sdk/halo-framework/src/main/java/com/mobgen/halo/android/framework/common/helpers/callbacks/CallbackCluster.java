package com.mobgen.halo.android.framework.common.helpers.callbacks;

import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Interface for the enhanced callbacks implementations.
 */
public interface CallbackCluster<C> {

    /**
     * Adds a new callback to the list.
     *
     * @param callback The callback to add.
     */
    @Api(1.0)
    void addCallback(@Nullable C callback);

    /**
     * Removes the callback from the list.
     *
     * @param callback The callback to remove.
     */
    @Api(1.0)
    void removeCallback(@Nullable C callback);

    /**
     * Notifies with the given arguments to the callbacks.
     *
     * @param args The arguments for the callbacks.
     */
    @Api(1.0)
    void notifyCallbacks(@Nullable Object... args);

    /**
     * Clears all the callbacks available.
     */
    @Api(1.0)
    void clear();

    /**
     * Makes the notification to the callback received as a parameter. It allows safe
     * callback with weak reference.
     *
     * @param callback The callback.
     * @param args     The arguments for the callback.
     */
    @Api(1.0)
    void notifyCallback(C callback, Object... args);
}
