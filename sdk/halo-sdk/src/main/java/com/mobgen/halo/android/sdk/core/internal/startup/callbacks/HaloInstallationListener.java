package com.mobgen.halo.android.sdk.core.internal.startup.callbacks;

import android.support.annotation.Keep;
import android.support.annotation.MainThread;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Allows to listen for the installation process of modules.
 */
@Keep
public interface HaloInstallationListener {

    /**
     * Callback to detect when the installation has finished.
     */
    @Api(1.0)
    @MainThread
    void onFinishedInstallation();
}
