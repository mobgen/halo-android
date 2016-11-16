package com.mobgen.halo.android.sdk.core.internal.startup.callbacks;

import android.support.annotation.Keep;
import android.support.annotation.MainThread;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Checks if the HALO SDK is ready. You should not perform operations without
 * being sure the SDK is completely working. Once you attach a listener it can be
 * synchronous or asynchronous depending if HALO is installed or not. If it is
 * installed you will receive the call immediately, otherwise you will have to wait.
 * <p>
 */
@Keep
public interface HaloReadyListener {

    /**
     * Callback that determines that Halo is ready to be used.
     */
    @Api(1.0)
    @MainThread
    void onHaloReady();
}
