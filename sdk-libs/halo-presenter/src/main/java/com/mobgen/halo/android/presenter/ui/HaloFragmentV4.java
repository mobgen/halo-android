package com.mobgen.halo.android.presenter.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.presenter.HaloPresenter;
import com.mobgen.halo.android.presenter.HaloPresenterManager;
import com.mobgen.halo.android.presenter.HaloViewTranslator;
import com.mobgen.halo.android.presenter.PresenterLifeCycleHandler;
import com.mobgen.halo.android.presenter.HaloPresenterException;

/**
 * Presenter fragment based on the compatibility library. Keeps the lifecycle of the presenter
 * in sync with the fragment lifecycle.
 *
 * @param <P> The type of the presenter attached to this presenter.
 */
@Keep
public abstract class HaloFragmentV4<P extends HaloPresenter> extends Fragment implements HaloViewTranslator, PresenterLifeCycleHandler<P> {

    /**
     * The presenter lifecycle manager.
     */
    private HaloPresenterManager<P> mPresenterManager;

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenterManager = new HaloPresenterManager<>(this);
        mPresenterManager.doCreate(savedInstanceState);
        mPresenterManager.presenter().setViewTranslator(this);
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onPresenterCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Creates the view for the given fragment.
     *
     * @param inflater           The inflater for the current view.
     * @param container          The container that will be used for the params of the view.
     * @param savedInstanceState The saved instance state.
     * @return The view created.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public abstract View onPresenterCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenterManager.doInit(savedInstanceState);
        onViewCreated(savedInstanceState);
        mPresenterManager.doLoad();
    }

    /**
     * Called when the view is completely setup. This is the correct place to set up the click listeners and other
     * interaction actions.
     *
     * @param savedInstanceState The bundle instance state.
     */
    @Keep
    @Api(2.0)
    public abstract void onViewCreated(Bundle savedInstanceState);

    @Override
    public void onNetworkConnected() {
        //Override to avoid child override
    }

    @Override
    public void onNetworkLost() {
        //Override to avoid child override
    }

    @Override
    public void showError(@NonNull HaloPresenterException exception) {
        throw new HaloConfigurationException("Override it! It is not implemented!");
    }

    @Override
    public void startLoading(int elementLoaderId) {
        throw new HaloConfigurationException("Override it! It is not implemented!");
    }

    @Override
    public void stopLoading(int elementLoaderId) {
        throw new HaloConfigurationException("Override it! It is not implemented!");
    }

    @Override
    public final void onResume() {
        super.onResume();
        mPresenterManager.doResume();
    }

    @Override
    public final void onPause() {
        super.onPause();
        mPresenterManager.doPause();
    }

    @Override
    public final void onDestroy() {
        mPresenterManager.doShutdown();
        super.onDestroy();
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(mPresenterManager.doSaveInstance(outState));
    }

    @NonNull
    @Override
    public final Context getContext() {
        return getActivity();
    }

    @Override
    public void hideKeyboard() {
        HaloUtils.hideKeyboard(getActivity().getCurrentFocus());
    }

    /**
     * Provides the current presenter.
     *
     * @return The current presenter created.
     */
    @Keep
    @Api(2.0)
    public final P presenter() {
        return mPresenterManager.presenter();
    }

    /**
     * Provides the presenter state of for the fragment.
     *
     * @return The presenter state.
     */
    @Keep
    @Api(2.0)
    public final HaloPresenterManager.PresenterState getPresenterState() {
        return mPresenterManager.getPresenterState();
    }

    @NonNull
    @Override
    public FragmentManager getSupportFragmentManager() {
        return getFragmentManager();
    }

    @Override
    public boolean isFinishing() {
        return getActivity().isFinishing();
    }

    @Override
    public final boolean isViewAvailable() {
        return mPresenterManager.isViewAvailable();
    }
}
