package com.mobgen.halo.android.framework.network.exceptions;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.IOException;

/**
 * Networking exception.
 */
public abstract class HaloNetException extends IOException {

    /**
     * Creates the exception from a message.
     *
     * @param message The message of its cause.
     */
    @Api(1.0)
    public HaloNetException(String message) {
        super(message);
    }

    /**
     * Creates the exception from another exception and a message.
     *
     * @param exception The base exception.
     * @param message   The message to display.
     */
    @Api(1.0)
    public HaloNetException(String message, Exception exception) {
        super(message, exception);
    }
}
