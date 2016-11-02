package com.mobgen.halo.android.sdk.core.internal.startup.processes;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.ProcessListener;

/**
 * Base class for a startup process.
 */
@Keep
public abstract class StartupProcess {

    /**
     * Listener for the process.
     */
    private ProcessListener mListener;
    /**
     * Tells if the process has finished.
     */
    private boolean mIsFinished;

    /**
     * Notifies that the process is finished. It will be automatically called at the end of the operations
     * in the startup.
     */
    @Api(1.3)
    protected final void notifyFinished() {
        if (!mIsFinished) {
            mIsFinished = true;
            mListener.onProcessFinished();
        }
    }

    /**
     * Sets the process listener.
     *
     * @param processListener The listener.
     */
    public final void setProcessListener(@NonNull ProcessListener processListener) {
        AssertionUtils.notNull(processListener, "process listener");
        mListener = processListener;
    }

    /**
     * Provides the thread policy for this startup process.
     *
     * @return The thread policy.
     */
    @Api(1.3)
    @Threading.Policy
    public abstract int getThreadPolicy();

    /**
     * Performs synchronous operations to notify for the startup process.
     *
     * @param halo The halo instance.
     */
    @Api(1.3)
    protected abstract void onStart(@NonNull Halo halo);
}
