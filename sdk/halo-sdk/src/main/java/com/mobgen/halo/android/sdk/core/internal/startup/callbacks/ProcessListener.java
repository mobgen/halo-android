package com.mobgen.halo.android.sdk.core.internal.startup.callbacks;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Listener used internally by halo to determine if a middleware has been installed
 * completely.
 */
@Keep
public interface ProcessListener {

    /**
     * Tells the installation process has finished.
     */
    @Api(1.3)
    void onProcessFinished();
}
