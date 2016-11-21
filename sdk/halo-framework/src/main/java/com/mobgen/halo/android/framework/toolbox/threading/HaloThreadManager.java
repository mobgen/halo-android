package com.mobgen.halo.android.framework.toolbox.threading;

import android.support.annotation.NonNull;

import java.util.concurrent.Future;

/**
 * Interface for the toolbox threadPolicy.
 */
public abstract class HaloThreadManager {

    /**
     * Enqueues a random runnable.
     *
     * @param thread   The threading policy.
     * @param runnable The runnable to enqueue.
     * @return The future created after enqueue.
     */
    public abstract Future enqueue(@Threading.Policy int thread, @NonNull Runnable runnable);
}
