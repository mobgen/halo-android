package com.mobgen.halo.android.framework.common.exceptions;

import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Halo security exception produced by some un-secure or erroneous action.
 */
public class HaloSecurityException extends RuntimeException {

    /**
     * Security exception constructor.
     *
     * @param message The message for the exception.
     * @param e       The exception that produced it.
     */
    @Api(1.0)
    public HaloSecurityException(@Nullable String message, @Nullable Exception e) {
        super(message, e);
    }
}
