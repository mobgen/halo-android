package com.mobgen.halo.android.framework.network.exceptions;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Networking exception that indicates the resource we are trying to get is not found.
 */
public class HaloNotFoundException extends HaloNetException {

    /**
     * The constructor exception.
     *
     * @param message The message for the exception.
     */
    @Api(1.0)
    public HaloNotFoundException(String message) {
        super(message);
    }
}
