package com.mobgen.halo.android.framework.network.exceptions;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Exception produced when the device has not connectivity which means the request can not be performed.
 */
public class HaloConnectionException extends HaloNetException {

    /**
     * The constructor for this exception.
     *
     * @param message   The message for this exception.
     * @param exception The parent exception.
     */
    @Api(1.0)
    public HaloConnectionException(String message, Exception exception) {
        super(message, exception);
    }
}
