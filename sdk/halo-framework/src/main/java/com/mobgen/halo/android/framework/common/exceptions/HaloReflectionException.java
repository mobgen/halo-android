package com.mobgen.halo.android.framework.common.exceptions;

import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Reflection exception produced when something went wrong doing a reflection
 * operation.
 */
public class HaloReflectionException extends RuntimeException {

    /**
     * The constructor of the reflection exception.
     *
     * @param message The message to show.
     * @param e       Exception produced.
     */
    @Api(1.0)
    public HaloReflectionException(@Nullable String message, @Nullable Exception e) {
        super(message, e);
    }
}
