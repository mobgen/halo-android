package com.mobgen.halo.android.sdk.core.internal.startup;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloInstallationListener;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.ProcessListener;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupProcess;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupRunnableAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the startup process to handle the different actions that should be performed
 * in the sdk.
 */
public class StartupManager implements ProcessListener {
    /**
     * The core.
     */
    private Halo mHalo;
    /**
     * The callback to notify results.
     */
    private HaloInstallationListener mInstallationListener;
    /**
     * Counter for pending processes.
     */
    private AtomicInteger mCounter;
    /**
     * Checks if the manager has started.
     */
    private boolean mIsRunning;
    /**
     * Checks if the startup process is finished.
     */
    private boolean mIsFinished;
    /**
     * The handler for the main thread.
     */
    private Handler mHandler;

    /**
     * The startup manager constructor that can handle the startup process.
     *
     * @param halo The halo instance.
     */
    public StartupManager(@NonNull Halo halo) {
        AssertionUtils.notNull(halo, "halo");
        mHalo = halo;
        mCounter = new AtomicInteger(0);
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Tells if the process has finished.
     *
     * @return True if finished, false otherwise.
     */
    @Api(1.3)
    public boolean isRunning() {
        return mIsRunning;
    }

    /**
     * Tells if the startup process has finished.
     *
     * @return True if finished, false otherwise.
     */
    @Api(1.3)
    public boolean hasFinished() {
        return mIsFinished;
    }

    /**
     * Adds an installation listener to handle the process of the installation.
     *
     * @param listener The listener.
     */
    @Api(2.0)
    public void setInstallationListener(@Nullable HaloInstallationListener listener) {
        mInstallationListener = listener;
    }

    /**
     * Starts the process of the startup.
     *
     * @param processes The processes to execute.
     */
    @Api(1.3)
    public synchronized void execute(@Nullable StartupProcess... processes) {
        if (processes == null || processes.length == 0) {
            notifyFinished();
        } else {
            runProcesses(processes);
        }
    }

    @Override
    public synchronized void onProcessFinished() {
        Halog.d(getClass(), "-- Process finished");
        if (mCounter.decrementAndGet() == 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyFinished();
                }
            });
        }
    }

    /**
     * Notifies the callbacks and clears the callbacks to avoid keeping references
     * from them.
     */
    private synchronized void notifyFinished() {
        if (!mIsFinished) {
            mIsFinished = true;
            mIsRunning = false;
            mInstallationListener.onFinishedInstallation();
            Halog.d(getClass(), "--- HALO SETUP FINISHED ---");
        }
    }

    /**
     * Runs the processes stored.
     */
    private synchronized void runProcesses(@NonNull StartupProcess... processes) {
        mIsFinished = false;
        mIsRunning = true;
        for (StartupProcess process : processes) {
            mCounter.incrementAndGet();
            //Set listener
            process.setProcessListener(this);
            //Run
            StartupRunnableAdapter runnable = new StartupRunnableAdapter(mHalo, process);
            mHalo.framework().toolbox().queue().enqueue(process.getThreadPolicy(), runnable);
        }
    }
}
