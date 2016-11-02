package com.mobgen.halo.android.presenter;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;


/**
 * Abstract simplification of the presenter interface.
 *
 * @param <T> The type of the HaloViewTranslator associated with this presenter.
 */
@Keep
public abstract class AbstractHaloPresenter<T extends HaloViewTranslator> implements HaloPresenter<T>, ConnectionBroadcastReceiver.NetworkStateListener {

    /**
     * View translator instance.
     */
    protected T mView;

    /**
     * Connection manager receiver.
     */
    private ConnectionBroadcastReceiver mNetworkReceiver;

    /**
     * Cosntructor of the presenter.
     *
     * @param viewTranslator The view translator created.
     */
    public AbstractHaloPresenter(@NonNull T viewTranslator) {
        mView = viewTranslator;
    }

    /**
     * Modifies the view translator added,
     *
     * @param viewTranslator The view translator added.
     */
    public void setViewTranslator(T viewTranslator) {
        mView = viewTranslator;
    }

    @Override
    @CallSuper
    public void onInitStarted(@Nullable Bundle savedInstanceState) {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new ConnectionBroadcastReceiver(this);
        }
        mView.getContext().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    @CallSuper
    public Bundle onSaveInstanceState(@NonNull Bundle bundle) {
        //Overrided to keep children simple
        return bundle;
    }

    @Override
    @CallSuper
    public void onPresenterResumed() {
        //Overrided to keep children simple
    }

    @Override
    @CallSuper
    public void onPresenterPaused() {
        //Overrided to keep children simple
    }

    @Override
    @CallSuper
    public void onPresenterShutdown() {
        //Overrided to keep children simple
        if (mNetworkReceiver != null) {
            mView.getContext().unregisterReceiver(mNetworkReceiver);
            mNetworkReceiver = null;
        }
    }

    @Override
    public void onNetworkStateChangedTo(boolean connected) {
        if (connected) {
            mView.onNetworkConnected();
        } else {
            mView.onNetworkLost();
        }
    }

    /**
     * Provides the network receiver.
     *
     * @return The network receiver.
     */
    @Nullable
    @VisibleForTesting
    public ConnectionBroadcastReceiver getNetworkReceiver() {
        return mNetworkReceiver;
    }

    /**
     * Provides the view associated with the presenter.
     *
     * @return The view translator associated.
     */
    @NonNull
    public final T getView() {
        return mView;
    }
}
