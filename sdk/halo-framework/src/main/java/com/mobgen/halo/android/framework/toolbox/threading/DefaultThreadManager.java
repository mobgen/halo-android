package com.mobgen.halo.android.framework.toolbox.threading;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is in charge to handle the execution in parallel or synchronously
 * of the actions generated.
 */
public class DefaultThreadManager extends HaloThreadManager {

    /**
     * Time to keep an idle thread if the size has expired.
     */
    private static final int KEEP_ALIVE_TIME = 1;

    /**
     * The number unit in seconds to wait.
     */
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * The number of cores available.
     */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * The number of cores.
     */
    private static final int CORE_POOL_SIZE = NUMBER_OF_CORES + 1;

    /**
     * The maximum number of processes running in the pool.
     */
    private static final int MAXIMUM_POOL_SIZE = NUMBER_OF_CORES * 2 + 1;
    /**
     * The pool threadPolicy.
     */
    private ExecutorService mPoolQueue;
    /**
     * The single threadPolicy executing.
     */
    private ExecutorService mSingleQueue;

    /**
     * Constructor to manage the threadPolicy.
     */
    public DefaultThreadManager() {
        mSingleQueue = Executors.newSingleThreadExecutor();
        final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        mPoolQueue = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, workQueue);
    }

    @Override
    public Future enqueue(@Threading.Policy int thread, @NonNull Runnable runnable) {
        Future<?> future;
        switch (thread) {
            case Threading.POOL_QUEUE_POLICY:
                future = mPoolQueue.submit(runnable);
                break;
            case Threading.SINGLE_QUEUE_POLICY:
                future = mSingleQueue.submit(runnable);
                break;
            case Threading.SAME_THREAD_POLICY:
                future = new FutureTask<>(runnable, null);
                runnable.run();
                break;
            default:
                throw new HaloConfigurationException("Unsupported option or an Action operation");
        }
        return future;
    }
}
