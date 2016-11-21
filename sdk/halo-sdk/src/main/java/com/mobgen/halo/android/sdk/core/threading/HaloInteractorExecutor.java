package com.mobgen.halo.android.sdk.core.threading;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;

/**
 * Executes some operation based on thread policies and ensuring halo is ready to execute the given
 * operation.
 */
@Keep
public final class HaloInteractorExecutor<T> implements ICancellable, ThreadContext<T> {

    /**
     * The halo instance.
     */
    @NonNull
    private Halo mHalo;
    /**
     * The callback on which the result will be reported.
     */
    @Nullable
    private CallbackV2<T> mCallback;
    /**
     * The thread policy that will be stored.
     */
    @Threading.Policy
    private int mThreadPolicy;
    /**
     * Flag to state if the selector has been cancelled.
     */
    private boolean mCancelled;
    /**
     * Bypasses the ready check.
     */
    private boolean mBypassReadyCheck;
    /**
     * The name of the request selector so we can log the results and process.
     */
    private String mName;
    /**
     * Execution callback.
     */
    @Nullable
    private InteractorExecutionCallback mExecutionCallback;
    /**
     * The operation that will be executed.
     */
    @NonNull
    private Interactor<T> mInteractor;
    /**
     * Where to post the result.
     */
    private Handler mResultHandler;

    /**
     * Creates the thread manager executor. It handles the execution of an interactor
     * that results in some data. This also handles the thread configuration and
     * provides an api that can be used by the device.
     *
     * @param halo       The halo instance.
     * @param name       The name of the interactor.
     * @param interactor The interactor to handle.
     */
    public HaloInteractorExecutor(@NonNull Halo halo,
                                  @NonNull String name,
                                  @NonNull final Interactor<T> interactor) {
        this(halo, name, interactor, null);
    }

    /**
     * Creates the thread manager executor. It handles the execution of an interactor
     * that results in some data. This also handles the thread configuration and
     * provides an api that can be used by the device.
     *
     * @param halo              The halo instance.
     * @param executionCallback The callback on which the result will be dispatched.
     * @param name              The name of the interactor.
     * @param interactor        The interactor to handle.
     */
    public HaloInteractorExecutor(@NonNull Halo halo,
                                  @NonNull String name,
                                  @NonNull final Interactor<T> interactor,
                                  @Nullable InteractorExecutionCallback executionCallback) {
        AssertionUtils.notNull(halo, "halo");
        AssertionUtils.notNull(name, "name");
        AssertionUtils.notNull(interactor, "interactor");
        mHalo = halo;
        mName = name;
        mInteractor = interactor;
        mExecutionCallback = executionCallback;
        mThreadPolicy = Threading.POOL_QUEUE_POLICY;
        if(Looper.myLooper() != null){
            mResultHandler = new Handler(Looper.myLooper());
        }
    }

    /**
     * Executes a given operation into the thread manager.
     *
     * @param callback The callback where we should dispatch the result of the operation.
     * @return A cancellable interface. In this case it is the same
     * executor but casted.
     */
    @NonNull
    @Api(2.0)
    public final ICancellable execute(@Nullable final CallbackV2<T> callback) {
        mCallback = callback;
        mHalo.framework().toolbox().queue().enqueue(mThreadPolicy,
                new SafeRunnable<T>(mHalo, mBypassReadyCheck, mName, mCallback) {
                    @Override
                    protected void safeRun() throws Exception {
                        HaloResultV2<T> resultingData = null;
                        if (mExecutionCallback != null) {
                            mExecutionCallback.onPreExecute();
                        }
                        if (!isCancelled()) {
                            resultingData = mInteractor.executeInteractor();
                        }
                        if (mExecutionCallback != null) {
                            mExecutionCallback.onPostExecute();
                        }
                        if (!isCancelled()) {
                            notifyEnded(resultingData);
                        }
                    }
                });
        return this;
    }

    /**
     * Executes a given operation into the thread manager.
     *
     * @return A cancellable interface. In this case it is the same
     * executor but casted.
     */
    @NonNull
    @Api(2.0)
    public final ICancellable execute() {
        execute(null);
        return this;
    }

    /**
     * Checks if the selector is cancelled.
     *
     * @return True if cancelled, false otherwise.
     */
    @Api(2.0)
    public boolean isCancelled() {
        return mCancelled;
    }

    /**
     * Cancels this request.
     */
    @Api(2.0)
    @Override
    public void cancel() {
        mCancelled = true;
        mCallback = null;
    }

    /**
     * Provides the thread policy.
     *
     * @param threadPolicy The policy.
     * @return The current executor.
     */
    @NonNull
    @Api(2.0)
    public HaloInteractorExecutor<T> threadPolicy(@Threading.Policy int threadPolicy) {
        mThreadPolicy = threadPolicy;
        return this;
    }

    /**
     * Bypasses the halo ready check. This check is done to ensure
     * the halo instance we are using is perfectly configured.
     *
     * @return The thread manager.
     */
    @Api(2.0)
    @NonNull
    public HaloInteractorExecutor<T> bypassHaloReadyCheck() {
        mBypassReadyCheck = true;
        return this;
    }

    /**
     * Notifies the request has finished.
     *
     * @param resultingData The resulting data after performing the operation.
     */
    private void notifyEnded(final HaloResultV2<T> resultingData) {
        //If we have a handler we know exactly where to report the data
        //In case the policy is the same thread, we just can avoid the context switching
        if (mResultHandler != null && mThreadPolicy != Threading.SAME_THREAD_POLICY) {
            mResultHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyCallback(resultingData);
                }
            });
        } else {
            notifyCallback(resultingData);
        }
    }

    /**
     * Notify the callback in a safe way, just in case this callback is null.
     *
     * @param resultingData The data to notify.
     */
    private void notifyCallback(@NonNull HaloResultV2<T> resultingData) {
        if (mCallback != null) {
            mCallback.onFinish(resultingData);
        }
    }

    /**
     * Executes the operation providing a result.
     */
    @Keep
    public interface Interactor<T> {
        /**
         * The operation to execute.
         *
         * @return The result generated.
         * @throws Exception Can throw any exception during the execution.
         */
        @Keep
        @Api(2.0)
        @NonNull
        HaloResultV2<T> executeInteractor() throws Exception;
    }

    /**
     * Runnable that ensures the errors produced during the execution of the
     * request are not dropped out, avoiding the app to crash.
     */
    private abstract static class SafeRunnable<T> implements Runnable {

        /**
         * The name of the context request that allows us to
         * keep tracking of the possible errors with clients.
         */
        private String mName;
        /**
         * The halo instance.
         */
        private Halo mHalo;
        /**
         * Bypasses the ready check.
         */
        private boolean mBypassReadyCheck;
        /**
         * Callback on which we will drop the result.
         */
        private CallbackV2<T> mCallback;

        /**
         * Constructor for the runnable with the name and callback.
         *
         * @param halo             The halo instance.
         * @param bypassReadyCheck Avoids executing the ready check.
         * @param name             The name.
         * @param callback         The callback.
         */
        public SafeRunnable(@NonNull Halo halo, boolean bypassReadyCheck, @NonNull String name, @Nullable CallbackV2<T> callback) {
            AssertionUtils.notNull(name, "name");
            mHalo = halo;
            mBypassReadyCheck = bypassReadyCheck;
            mName = name;
            mCallback = callback;
        }

        @Override
        public void run() {
            Halog.d(getClass(), "Executing Halo request -> " + mName);
            if (mBypassReadyCheck) {
                wrapSafely();
            } else {
                mHalo.ready(new HaloReadyListener() {
                    @Override
                    public void onHaloReady() {
                        wrapSafely();
                    }
                });
            }
        }

        /**
         * Wraps capturing all the possible exceptions that might occur while executing something in halo.
         */
        private void wrapSafely() {
            try {
                long timeBefore = System.nanoTime();
                safeRun();
                Halog.d(getClass(), "Halo request -> " + mName + " took " + (System.nanoTime() - timeBefore) / 1000000 + "ms");
            } catch (Exception e) { // Caught unexpected exceptions
                Halog.e(getClass(), "Halo request -> Unexpected exception on call " + mName + " with error message " + e.getMessage(), e);
                if (mCallback != null) {
                    //Notify erroneous execution
                    HaloStatus status = HaloStatus.builder()
                            .error(e)
                            .build();
                    mCallback.onFinish(new HaloResultV2<T>(status, null));
                }
            }
        }


        /**
         * Method that is wrapped into a global try catch. This avoids dropping unhandled
         * exceptions to the application.
         *
         * @throws Exception The exception produced.
         */
        @Api(2.0)
        protected abstract void safeRun() throws Exception;
    }
}
