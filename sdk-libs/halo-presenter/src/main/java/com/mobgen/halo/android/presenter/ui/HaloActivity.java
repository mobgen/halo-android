package com.mobgen.halo.android.presenter.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 * Halo activity that handles the presenter lifecycle.
 *
 * @param <P> The type of the presenter.
 */
@Keep
public abstract class HaloActivity<P extends HaloPresenter> extends Activity implements PresenterLifeCycleHandler<P>, HaloViewTranslator {

    /**
     * Presenter manager.
     */
    private HaloPresenterManager<P> mPresenterManager;

    @Override
    @SuppressWarnings("unchecked")
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenterManager = new HaloPresenterManager<>(this);
        mPresenterManager.doCreate(savedInstanceState);
        mPresenterManager.presenter().setViewTranslator(this);
        setContentView(onPresenterCreateView(LayoutInflater.from(this), (ViewGroup) getWindow().getDecorView(), savedInstanceState));
        mPresenterManager.doInit(savedInstanceState);
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onViewCreated(savedInstanceState);
        mPresenterManager.doLoad();
    }

    @Override
    protected final void onPause() {
        super.onPause();
        mPresenterManager.doPause();
    }

    @Override
    protected final void onResume() {
        super.onResume();
        mPresenterManager.doResume();
    }

    @Override
    protected final void onDestroy() {
        mPresenterManager.doShutdown();
        super.onDestroy();
    }

    @Override
    protected final void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(mPresenterManager.doSaveInstance(outState));
    }

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

    @NonNull
    @Override
    public final Context getContext() {
        return this;
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
    public void hideKeyboard() {
        HaloUtils.hideKeyboard(getCurrentFocus());
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
     * Provides the presenter state of for the activity.
     *
     * @return The presenter state.
     */
    @Keep
    @Api(2.0)
    public final HaloPresenterManager.PresenterState getPresenterState() {
        return mPresenterManager.getPresenterState();
    }

    @Override
    public final boolean isViewAvailable() {
        return mPresenterManager.isViewAvailable();
    }
}
