package com.mobgen.halo.android.sdk.core.internal.startup;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.callbacks.CallbackCluster;
import com.mobgen.halo.android.framework.common.helpers.callbacks.StrongCallbackCluster;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloInstallationListener;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;

import java.util.concurrent.CountDownLatch;

/**
 * The ready checker is the class in charge to make sure halo is ready to keep executing the pending tasks.
 */
public class ReadyChecker implements HaloInstallationListener {

    /**
     * The startup manager.
     */
    private StartupManager mStartupManager;

    /**
     * If we are on the main thread, then the checks are quite different.
     */
    private MainThreadContextChecker mMainThreadContextChecker;

    /**
     * The background thread checker.
     */
    private BackgroundThreadContextChecked mBackgroundContextChecker;

    /**
     * Constructor for the ready checker.
     *
     * @param startupManager The startup manager that handles the startup process.
     */
    public ReadyChecker(@NonNull StartupManager startupManager) {
        AssertionUtils.notNull(startupManager, "startupManager");
        mStartupManager = startupManager;

        mMainThreadContextChecker = new MainThreadContextChecker(new Handler(Looper.getMainLooper()));
        mBackgroundContextChecker = new BackgroundThreadContextChecked();

        mStartupManager.setInstallationListener(this);
    }

    /**
     * Performs the ready checks needed to make sure everything works fine.
     *
     * @param readyListener The ready listener that wil be executed.
     * @throws InterruptedException
     */
    public void checkReady(@NonNull HaloReadyListener readyListener) throws InterruptedException {
        AssertionUtils.notNull(readyListener, "readyListener");
        if (mStartupManager.hasFinished()) {
            readyListener.onHaloReady();
        } else {
            boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
            if (isMainThread) {
                mMainThreadContextChecker.enqueue(readyListener);
            } else {
                mBackgroundContextChecker.enqueue(readyListener);
                readyListener.onHaloReady();
            }
        }
    }

    @Override
    public void onFinishedInstallation() {
        mMainThreadContextChecker.notifyReady();
        mBackgroundContextChecker.notifyReady();
    }

    /**
     * The thread checker.
     */
    private interface ThreadChecker {
        /**
         * Performs the ready checks needed to make sure everything works fine.
         *
         * @param readyListener The ready listener that wil be executed.
         */
        void enqueue(@NonNull HaloReadyListener readyListener) throws InterruptedException;

        /**
         * Notifies that the installation pending has finished.
         */
        void notifyReady();
    }

    /**
     * Background thread checker to make sure everything is executed in the proper moment.
     */
    private static class BackgroundThreadContextChecked implements ThreadChecker {

        /**
         * Concurrency item to make sure we signal stopped threads.
         */
        private CountDownLatch mConcurrentSignal;

        /**
         * Constructor for the background checker.
         */
        public BackgroundThreadContextChecked() {
            mConcurrentSignal = new CountDownLatch(1);
        }

        @Override
        public void enqueue(@NonNull HaloReadyListener readyListener) throws InterruptedException {
            mConcurrentSignal.await();
        }

        @Override
        public void notifyReady() {
            mConcurrentSignal.countDown();
        }
    }

    /**
     * Main thread checker.
     */
    private static class MainThreadContextChecker implements ThreadChecker {

        /**
         * The handler for the main thread checker.
         */
        private final Handler mHandler;

        /**
         * Callback cluster to add as many callbacks as needed.
         */
        private CallbackCluster<HaloReadyListener> mReadyListeners;

        /**
         * Constructor for the context checker.
         *
         * @param handler The main thread handler.
         */
        public MainThreadContextChecker(@NonNull Handler handler) {
            mHandler = handler;
            mReadyListeners = new StrongCallbackCluster<HaloReadyListener>() {
                @Override
                public void notifyCallback(HaloReadyListener callback, Object... args) {
                    postCallbackReady(callback);
                }
            };
        }

        /**
         * Posts the ready callback to the handler.
         * @param callback The callback.
         */
        private void postCallbackReady(@NonNull final HaloReadyListener callback) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onHaloReady();
                }
            });
        }

        @Override
        public void enqueue(@NonNull HaloReadyListener readyListener) {
            mReadyListeners.addCallback(readyListener);
        }

        @Override
        public void notifyReady() {
            mReadyListeners.notifyCallbacks();
            mReadyListeners.clear();
        }
    }
}
