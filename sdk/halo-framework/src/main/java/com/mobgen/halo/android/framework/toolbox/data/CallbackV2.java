package com.mobgen.halo.android.framework.toolbox.data;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * The callback that will be used for the data synchronization framework.
 */
public interface CallbackV2<T> {

    /**
     * Provides the data obtained in the request.
     *
     * @param result The data result for this operation.
     */
    @Api(2.0)
    @MainThread
    void onFinish(@NonNull HaloResultV2<T> result);
}