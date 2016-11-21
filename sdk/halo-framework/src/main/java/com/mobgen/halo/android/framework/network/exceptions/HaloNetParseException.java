package com.mobgen.halo.android.framework.network.exceptions;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Exception produced when the parse process fails on the response produced by
 * a web service.
 */
public class HaloNetParseException extends HaloNetException {

    /**
     * The parsing exception produced.
     *
     * @param message The message describing the problem.
     * @param e       Root exception.
     */
    @Api(1.0)
    public HaloNetParseException(String message, Exception e) {
        super(message, e);
    }
}
