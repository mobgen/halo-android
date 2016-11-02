package com.mobgen.halo.android.sdk.core.threading;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * ICancellable interface that supports cancelling the requests.
 */
@Keep
public interface ICancellable {
    /**
     * Cancels the request.
     */
    @Api(2.0)
    void cancel();
}