package com.mobgen.halo.android.framework.network.exceptions;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Error produced when a server throws errors of the 400 family according to http protocol.
 */
public class HaloAuthenticationException extends HaloNetException {

    /**
     * Constructor for the authentication exception.
     *
     * @param message The message produced when the authentication failed.
     */
    @Api(1.0)
    public HaloAuthenticationException(String message) {
        super(message);
    }
}
