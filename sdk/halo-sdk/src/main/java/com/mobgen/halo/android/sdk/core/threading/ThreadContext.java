package com.mobgen.halo.android.sdk.core.threading;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

/**
 * Allows to set the thread policy for this context.
 */
@Keep
interface ThreadContext<T> {
    /**
     * The thread policy value.
     *
     * @param policy The policy.
     * @return The same thread context.
     */
    @NonNull
    @Api(2.0)
    ThreadContext<T> threadPolicy(@Threading.Policy int policy);

    /**
     * Executes the request.
     *
     * @param callback The callback for this request.
     * @return The cancellable request param.
     */
    @NonNull
    @Api(2.0)
    ICancellable execute(@Nullable CallbackV2<T> callback);
}