package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Base selector that dispatches the threading changes and callback execution for
 * the data selection process.
 */
@Keep
public abstract class Selector<T> implements HaloInteractorExecutor.Interactor<T> {
    /**
     * The data policy for the selector choosing.
     */
    @Data.Policy
    private int mDataPolicy;

    /**
     * Base selector that contains all the common data needed for all the
     * selectors.
     *
     * @param mode The mode selected.
     */
    public Selector(@Data.Policy int mode) {
        mDataPolicy = mode;
    }

    /**
     * Provides the data policy for this request.
     *
     * @return The data policy.
     */
    @Api(2.0)
    @Data.Policy
    public int dataPolicy() {
        return mDataPolicy;
    }
}