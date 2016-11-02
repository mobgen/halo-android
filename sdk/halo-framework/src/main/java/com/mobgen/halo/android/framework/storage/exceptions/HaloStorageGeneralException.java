package com.mobgen.halo.android.framework.storage.exceptions;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Error produced in the storage part of the framework.
 */
public class HaloStorageGeneralException extends HaloStorageException {
    /**
     * Creates the exception from a message.
     *
     * @param e       Generic error for the storage.
     * @param message The message of its cause.
     */
    @Api(1.0)
    public HaloStorageGeneralException(@NonNull String message, @NonNull Exception e) {
        super(message, e);
    }
}
