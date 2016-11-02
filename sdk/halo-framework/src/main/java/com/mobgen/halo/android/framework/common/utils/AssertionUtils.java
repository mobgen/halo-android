package com.mobgen.halo.android.framework.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Assertion utils that is able to throw exceptions in case of the assertion is not met.
 */
public final class AssertionUtils {

    /**
     * Private constructor to avoid instances.
     */
    private AssertionUtils() {
        //Private constructor for a static utils class.
    }

    /**
     * Ensures that the object passed is not null.
     *
     * @param object   The object nullable.
     * @param property The property.
     */
    public static void notNull(@Nullable Object object, @NonNull String property) {
        if (object == null) {
            throw new NullPointerException(property + " == null");
        }
    }
}
