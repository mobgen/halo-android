package com.mobgen.halo.android.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;

/**
 * The presenter manager that handles the callbacks for the presenter dispatcher. It allows to
 * have a callback linked to halo that ensures halo is loaded.
 *
 * @param <P> The presenter type.
 */
public class HaloPresenterManager<P extends HaloPresenter> {

    /**
     * Represents the current state of the presenter.
     */
    public enum PresenterState {
        /**
         * There is no presenter, so getPresenter() returns null.
         */
        NONE,
        /**
         * The presenter has been created and is prepared to
         * start the lifecycle. The view is still null in this state.
         */
        CREATED,
        /**
         * Marks the current presenter as init. In this moment the UI is loaded.
         */
        INIT,
        /**
         * Called when halo is fully loaded, so we don't need splash screen. or
         * a broken lifecycle. With this state the business operations can start.
         */
        LOADED,
        /**
         * The presenter has been destroyed and is no longer available. You should not
         * perform any action related to view instead of releasing resources or
         * recycling memory.
         */
        DESTROYED
    }

    /**
     * The presenter managed.
     */
    private P mPresenter;

    /**
     * Handles the lifecycle of the presenter owner in relation with the presenter.
     */
    private PresenterLifeCycleHandler<P> mLifecycleHandler;

    /**
     * Keeps the current state of the presenter.
     */
    private PresenterState mPresenterState;

    /**
     * The presenter manager containing the presenter and the lifecycle.
     *
     * @param lifecycleHandler The lifecycle handler.
     */
    public HaloPresenterManager(@NonNull PresenterLifeCycleHandler<P> lifecycleHandler) {
        mLifecycleHandler = lifecycleHandler;
        mPresenterState = PresenterState.NONE;
    }

    /**
     * Provides the presenter. It is created once the doCreate method has been called.
     *
     * @return The presenter or null if the doCreate method has not been called yet.
     */
    public P presenter() {
        return mPresenter;
    }

    /**
     * Dispatches the create action.
     *
     * @param savedIntanceState The saved instance state for the context element.
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    @SuppressWarnings("unchecked")
    public HaloPresenterManager doCreate(@Nullable Bundle savedIntanceState) {
        if (mPresenter == null) {
            mPresenter = mLifecycleHandler.createPresenter(savedIntanceState);
        }
        mPresenterState = PresenterState.CREATED;
        return this;
    }

    /**
     * Initializes the presenter.
     *
     * @param savedIntanceState The saved instance of the presenter.
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    public HaloPresenterManager doInit(@Nullable Bundle savedIntanceState) {
        ensureValidState();
        mPresenter.onInitStarted(savedIntanceState);
        mPresenterState = PresenterState.INIT;
        return this;
    }

    /**
     * Resumes the presenter.
     *
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    public HaloPresenterManager doResume() {
        ensureValidState();
        mPresenter.onPresenterResumed();
        return this;
    }

    /**
     * Makes the loading step and performs the checks against the halo framework.
     *
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    public HaloPresenterManager doLoad() {
        ensureValidState();
        try {
            if (Class.forName("com.mobgen.halo.android.sdk.api.Halo") != null && Halo.isInitialized()) {
                Halo.instance().ready(new HaloReadyListener() {
                    @Override
                    public void onHaloReady() {
                        onInitialize();
                    }
                });
            } else {
                //No halo installation so go just for presenter
                onInitialize();
            }
        } catch (ClassNotFoundException e) {
            onInitialize();
        }
        return this;
    }

    /**
     * Pauses the presenter.
     *
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    public HaloPresenterManager doPause() {
        ensureValidState();
        mPresenter.onPresenterPaused();
        return this;
    }

    /**
     * Shutdowns the presenter.
     *
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    @SuppressWarnings("unchecked")
    public HaloPresenterManager doShutdown() {
        ensureValidState();
        mPresenter.onPresenterShutdown();
        mPresenterState = PresenterState.DESTROYED;
        //We remove the reference of the presenter. It will not live anymore
        mPresenter.setViewTranslator(null);
        return this;
    }

    /**
     * Saves the instance state of the presenter.
     *
     * @param outState The bundle state.
     * @return The instance of the presenter manager so we can manage the lifecycle.
     */
    public Bundle doSaveInstance(Bundle outState) {
        ensureValidState();
        return mPresenter.onSaveInstanceState(outState);
    }

    /**
     * Sets up the presenter calling all its methods.
     *
     * @param bundle The bundle saved instance state.
     * @return The presenter manager.
     */
    @VisibleForTesting
    public HaloPresenterManager doSetup(Bundle bundle) {
        doCreate(bundle).doInit(bundle).doLoad().doResume();
        return this;
    }

    /**
     * Provides the presenter current state.
     *
     * @return The current state of the inner presenter. It manages it using the callbacks from its activity.
     */
    public PresenterState getPresenterState() {
        return mPresenterState;
    }

    /**
     * Provides the current presenter on the manager.
     *
     * @return The current presenter.
     */
    @Nullable
    public P getPresenter() {
        return mPresenter;
    }

    /**
     * Gives the feedback to avoid writing in activities with destroyed scopes or invalid views, a common crash when an application is
     * in background.
     *
     * @return True if the state is a view available state, false otherwise.
     */
    public boolean isViewAvailable() {
        return mPresenterState != PresenterState.CREATED && mPresenterState != PresenterState.NONE && mPresenterState != PresenterState.DESTROYED;
    }

    /**
     * Mark the presenter as loaded.
     */
    private void onInitialize() {
        // The initialization can only be done if the activity / fragment is in a valid state
        // which means we can perform some actions on it
        if (mPresenterState == PresenterState.INIT) {
            mPresenter.onInitialized();
            mPresenterState = PresenterState.LOADED;
        }
    }

    /**
     * Ensures the presenter is not initialized and not destroyed.
     */
    private void ensureValidState() {
        if (mPresenterState == PresenterState.DESTROYED || mPresenterState == PresenterState.NONE) {
            throw new IllegalStateException("The presenter has been destroyed so you cannot do any operation in it.");
        }
    }
}
