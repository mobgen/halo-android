package com.mobgen.halo.android.framework.network.exceptions;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Exception produced in the network layer when a response contains a 500 family code.
 */
public class HaloServerException extends HaloNetException {

    /**
     * Code of the exception.
     */
    private final int mErrorCode;

    /**
     * The body with the error produced.
     */
    private final String mBody;

    /**
     * Constructor of the exception.
     *
     * @param message The message for this exception.
     * @param body    The body of the request that produced the error.
     * @param code    The code of the server error.
     */
    @Api(1.1)
    public HaloServerException(@NonNull String message, @NonNull String body, int code) {
        super(message);
        mBody = body;
        mErrorCode = code;
    }

    /**
     * Provides the error code produced on the server.
     *
     * @return The error code produced in the server.
     */
    @Api(1.0)
    public int getErrorCode() {
        return mErrorCode;
    }

    /**
     * Provides the body of the item.
     *
     * @return The body.
     */
    @Api(1.1)
    @NonNull
    public String getBody() {
        return mBody;
    }

    @Override
    public String toString() {
        return "Message: " + getMessage() + "\nError: " + mBody;
    }
}
