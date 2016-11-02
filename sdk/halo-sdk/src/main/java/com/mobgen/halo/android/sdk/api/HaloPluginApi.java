package com.mobgen.halo.android.sdk.api;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.sdk.core.HaloCore;

/**
 * Abstract class for plugins of halo.
 */
@Keep
public abstract class HaloPluginApi {

    /**
     * The halo instance.
     */
    @NonNull
    private final Halo mHalo;

    /**
     * Constructor for the halo plugin.
     *
     * @param halo The halo instance.
     */
    public HaloPluginApi(@NonNull Halo halo) {
        AssertionUtils.notNull(halo, "halo");
        mHalo = halo;
    }

    /**
     * Provides the instance of the framework.
     *
     * @return The instance of the framework.
     */
    @Api(1.3)
    @NonNull
    protected HaloFramework framework() {
        return mHalo.framework();
    }

    /**
     * Provides the halo instance stored.
     *
     * @return The halo instance stored.
     */
    @Api(1.3)
    @NonNull
    public Halo halo() {
        return mHalo;
    }

    /**
     * Provides the halo instance.
     *
     * @return The halo instance.
     */
    @Api(1.3)
    @NonNull
    public HaloCore core() {
        return mHalo.getCore();
    }

    /**
     * The context.
     *
     * @return The context.
     */
    @Api(1.3)
    @NonNull
    public Context context() {
        return mHalo.context();
    }
}
