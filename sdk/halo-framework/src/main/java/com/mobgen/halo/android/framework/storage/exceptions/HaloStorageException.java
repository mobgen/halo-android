package com.mobgen.halo.android.framework.storage.exceptions;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Storage exception.
 */
public abstract class HaloStorageException extends Exception {

    /**
     * Creates the exception from a message.
     *
     * @param message The message of its cause.
     * @param e       The exception wrapped.
     */
    @Api(1.0)
    public HaloStorageException(@NonNull String message, @NonNull Exception e) {
        super(message, e);
    }
}
