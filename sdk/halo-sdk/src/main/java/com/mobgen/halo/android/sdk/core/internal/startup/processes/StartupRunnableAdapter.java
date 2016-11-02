package com.mobgen.halo.android.sdk.core.internal.startup.processes;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Runnable to handle the startup process.
 */
public class StartupRunnableAdapter implements Runnable {

    /**
     * The core instance.
     */
    private final Halo mHalo;
    /**
     * The startup process.
     */
    private final StartupProcess mProcess;

    /**
     * Creates a runnable to use the process and the core together.
     *
     * @param halo    The halo instance.
     * @param process The process.
     */
    public StartupRunnableAdapter(@NonNull Halo halo, @NonNull StartupProcess process) {
        mHalo = halo;
        mProcess = process;
    }

    @Override
    public void run() {
        try {
            mProcess.onStart(mHalo);
        } catch (Exception e) {
            Halog.e(getClass(), "Error in startup process " + mProcess.getClass(), e);
        } finally {
            mProcess.notifyFinished();
        }
    }
}
