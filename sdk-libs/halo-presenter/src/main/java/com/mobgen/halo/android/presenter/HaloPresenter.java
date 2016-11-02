package com.mobgen.halo.android.presenter;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Presenter pattern for halo applications.
 *
 * @param <T> The type of the view translator for the current presenter.
 */
@Keep
public interface HaloPresenter<T extends HaloViewTranslator> {

    /**
     * Initializes the presenter. It is supposed to exist the UI when this method is called,
     * so we can perform any kind of action in the UI to initialize it.
     *
     * @param savedInstanceState The bundle to create the instance. It can be used
     *                           to restore the state.
     */
    @Keep
    @Api(2.0)
    void onInitStarted(@Nullable Bundle savedInstanceState);

    /**
     * Callback that tells us that all halo modules have been initialized
     * and we can perform any action related to the modules.
     * In this moment the UI is also saved and we can call any HaloViewTranslator
     * method.
     */
    @Keep
    @Api(2.0)
    void onInitialized();

    /**
     * Saves the instance state when a configuration change has been produced.
     *
     * @param bundle The bundle on which the data will be saved.
     * @return The bundle modified.
     */
    @Keep
    @Api(2.0)
    Bundle onSaveInstanceState(@NonNull Bundle bundle);

    /**
     * Releases all the resources allocated by the presenter and cancels all the pending
     * requests so this presenter will not be used anymore.
     */
    @Keep
    @Api(2.0)
    void onPresenterShutdown();

    /**
     * Called when the presenter is resumed.
     */
    @Keep
    @Api(2.0)
    void onPresenterResumed();

    /**
     * Called when the presenter is paused.
     */
    @Keep
    @Api(2.0)
    void onPresenterPaused();

    /**
     * Sets the view translator instance. This method is intended to be used by the framework.
     *
     * @param viewTranslator The view translator.
     */
    @Keep
    @Api(2.0)
    void setViewTranslator(@Nullable T viewTranslator);
}
