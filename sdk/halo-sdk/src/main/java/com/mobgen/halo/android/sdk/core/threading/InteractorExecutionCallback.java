package com.mobgen.halo.android.sdk.core.threading;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.sdk.core.selectors.HaloSelectorFactory;

/**
 * Callback to add a hook into the execution of the {@link HaloSelectorFactory}.
 */
@Keep
public abstract class InteractorExecutionCallback {
    /**
     * Callback called right before executing the request.
     */
    @Api(2.0)
    public void onPreExecute() {
        //Intended to be overriden if we need a hook
    }

    /**
     * Callback called right after executing a request.
     */
    @Api(2.0)
    public void onPostExecute() {
        //Intended to be overriden if we need a hook
    }
}