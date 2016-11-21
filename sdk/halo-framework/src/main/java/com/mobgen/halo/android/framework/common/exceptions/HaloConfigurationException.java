package com.mobgen.halo.android.framework.common.exceptions;

import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The HaloConfigurationException refers an error related to a configuration
 * mismatch. Please read the documentation.
 */
public class HaloConfigurationException extends RuntimeException {

    /**
     * Constructor for the configuration exception.
     *
     * @param message The message.
     * @param e       The exception.
     */
    @Api(1.0)
    public HaloConfigurationException(@Nullable String message, @Nullable Exception e) {
        super(message, e);
    }

    /**
     * Constructor for the halo configuration exception.
     *
     * @param message The message.
     */
    @Api(1.0)
    public HaloConfigurationException(@Nullable String message) {
        super(message);
    }
}
