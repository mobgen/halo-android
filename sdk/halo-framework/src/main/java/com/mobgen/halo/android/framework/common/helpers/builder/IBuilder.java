package com.mobgen.halo.android.framework.common.helpers.builder;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Builder interface used everywhere when we need to build something.
 */
public interface IBuilder<T> {

    /**
     * Typical interface to build something.
     *
     * @return The element built.
     */
    @Api(1.0)
    @NonNull
    T build();
}
