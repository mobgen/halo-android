package com.mobgen.halo.android.presenter;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The view translator is the interface for the presenter pattern that acts
 * as a view in MVP.
 */
@Keep
public interface HaloViewTranslator {

    /**
     * Provides a string given a resource.
     *
     * @param stringResource The resource.
     * @return The strign obtained.
     */
    @Keep
    @Api(2.0)
    String getString(@StringRes int stringResource);

    /**
     * Starts the loader for some process.
     *
     * @param elementLoaderId The id of the element that should start loading. This allows
     *                        to have the same method for all the loaders.
     */
    @Keep
    @Api(2.0)
    void startLoading(int elementLoaderId);

    /**
     * Stops the loader in the given element.
     *
     * @param elementLoaderId The element where the loader should stop.
     */
    @Keep
    @Api(2.0)
    void stopLoading(int elementLoaderId);

    /**
     * Shows the error in the given view element.
     *
     * @param exception The exception to show.
     */
    @Keep
    @Api(2.0)
    void showError(@NonNull HaloPresenterException exception);

    /**
     * Callback received when the network is lost.
     */
    @Keep
    @Api(2.0)
    void onNetworkLost();

    /**
     * Callback received wehn the network is reached again.
     */
    @Keep
    @Api(2.0)
    void onNetworkConnected();

    /**
     * Provides the context of the underlaying activity.
     *
     * @return The context.
     */
    @Keep
    @NonNull
    @Api(2.0)
    Context getContext();

    /**
     * Determines if the view translator is in a view state, which means the view has been created and the view translator
     * is available to perform actions on it. This method is useful to avoid writting on null view when they are not available
     * anymore.
     *
     * @return True if the view element is in a view state.
     */
    @Keep
    @Api(2.0)
    boolean isViewAvailable();

    /**
     * Hides the keyboard present on the view.
     */
    @Keep
    @Api(2.0)
    void hideKeyboard();
}
