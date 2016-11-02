package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;

/**
 * Exception while retrieving the cache info.
 */
public class CacheException extends Exception {

    public CacheException(@NonNull String message) {
        super(message);
    }

    public CacheException(@NonNull Exception e, @NonNull String message) {
        super(message, e);
    }
}
