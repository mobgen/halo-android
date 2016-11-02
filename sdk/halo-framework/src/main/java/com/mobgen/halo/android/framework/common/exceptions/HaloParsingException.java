package com.mobgen.halo.android.framework.common.exceptions;

import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Implies an error while parsing.
 */
public class HaloParsingException extends Exception {

    /**
     * Constructor for the configuration exception.
     *
     * @param message The message.
     * @param e       The exception.
     */
    @Api(1.4)
    public HaloParsingException(@Nullable String message, @Nullable Exception e) {
        super(message, e);
    }

}
