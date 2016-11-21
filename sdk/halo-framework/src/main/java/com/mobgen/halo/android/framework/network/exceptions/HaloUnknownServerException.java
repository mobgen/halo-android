package com.mobgen.halo.android.framework.network.exceptions;


import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Exception produced in the network layer when the request has failed with an unknown code or error
 * that can not be processed.
 */
public class HaloUnknownServerException extends HaloNetException {

    /**
     * Constructor of the exception.
     *
     * @param message   The message that will be used.
     * @param exception The original exception.
     */
    @Api(1.0)
    public HaloUnknownServerException(String message, Exception exception) {
        super(message, exception);
    }

    /**
     * Constructor of the exception.
     *
     * @param message The message that will be used.
     */
    @Api(1.0)
    public HaloUnknownServerException(String message) {
        super(message);
    }
}
