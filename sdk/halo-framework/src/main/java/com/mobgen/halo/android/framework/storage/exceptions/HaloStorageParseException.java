package com.mobgen.halo.android.framework.storage.exceptions;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Parse exception produced during the process of parse something from the internal storage.
 */
public class HaloStorageParseException extends HaloStorageException {

    /**
     * Creates the exception from a message.
     *
     * @param message The message of its cause.
     * @param e The exception to wrap.
     */
    @Api(1.0)
    public HaloStorageParseException(@NonNull String message, @NonNull Exception e) {
        super(message, e);
    }
}
