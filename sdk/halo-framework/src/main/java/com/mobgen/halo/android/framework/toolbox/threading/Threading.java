package com.mobgen.halo.android.framework.toolbox.threading;

import android.support.annotation.IntDef;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Threading class that contains the different strategies attached to
 * the engine.
 */
public final class Threading {

    private Threading() {
    }

    /**
     * Determines the policy to use when running the actions.
     */
    @IntDef({SINGLE_QUEUE_POLICY, POOL_QUEUE_POLICY, SAME_THREAD_POLICY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Policy {
    }

    /**
     * Adds the toolbox action to a pool of threads.
     */
    @Api(1.3)
    public static final int POOL_QUEUE_POLICY = 0;
    /**
     * Enqueues the toolbox to a single execution thread.
     */
    @Api(1.3)
    public static final int SINGLE_QUEUE_POLICY = 1;
    /**
     * Runs this action in the same thread.
     */
    @Api(1.3)
    public static final int SAME_THREAD_POLICY = 2;
}
