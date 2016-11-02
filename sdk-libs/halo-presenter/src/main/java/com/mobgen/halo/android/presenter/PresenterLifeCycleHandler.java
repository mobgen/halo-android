package com.mobgen.halo.android.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * @hide
 * Presenter lifecycle handler that provides callbacks for the lifecycle
 * of a presenter. Typically it will be implemented by an activity, a fragment
 * or other meta structure that needs to have a presenter, like very complex views.
 */
public interface PresenterLifeCycleHandler<T> {

    /**
     * Creates the presenter.
     *
     * @param savedInstaceState saved instance state of the context.
     * @return The presenter created.
     */
    @NonNull
    T createPresenter(@Nullable Bundle savedInstaceState);

    /**
     * Provides the fragment manager to handle the persistence of a presenter.
     *
     * @return The fragment manager.
     */
    @NonNull
    FragmentManager getSupportFragmentManager();

    /**
     * Checks if the element containing this view translator is finishing.
     *
     * @return True if this view translator is finishing.
     */
    boolean isFinishing();
}
