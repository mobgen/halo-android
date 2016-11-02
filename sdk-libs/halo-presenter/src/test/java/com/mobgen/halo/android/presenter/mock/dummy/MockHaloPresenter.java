package com.mobgen.halo.android.presenter.mock.dummy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.presenter.AbstractHaloPresenter;
import com.mobgen.halo.android.presenter.HaloViewTranslator;
import com.mobgen.halo.android.testing.BundleTestUtils;

import org.junit.Assert;

public class MockHaloPresenter extends AbstractHaloPresenter {
    boolean mInitialized;
    boolean mInit;
    boolean mConnected;
    boolean mPaused;
    boolean mResumed;
    boolean mShutdown;

    @SuppressWarnings("unchecked")
    public MockHaloPresenter(@NonNull HaloViewTranslator viewTranslator) {
        super(viewTranslator);
    }

    @Override
    public void onNetworkStateChangedTo(boolean connected) {
        super.onNetworkStateChangedTo(connected);
        mConnected = connected;
    }

    @Override
    public void onInitStarted(@Nullable Bundle savedInstanceState) {
        super.onInitStarted(savedInstanceState);
        mInit = true;
        if (savedInstanceState != null) {
            Assert.assertEquals("mock", savedInstanceState.getString("mock"));
        }
    }

    @Override
    public void onInitialized() {
        mInitialized = true;
    }

    @Override
    public void onPresenterPaused() {
        super.onPresenterPaused();
        mPaused = true;
        mResumed = false;
    }

    @Override
    public void onPresenterResumed() {
        super.onPresenterResumed();
        mResumed = true;
        mPaused = false;
    }

    @Override
    public void onPresenterShutdown() {
        super.onPresenterShutdown();
        mShutdown = true;
    }

    @Override
    public Bundle onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Assert.assertEquals("mock", bundle.getString("mock"));
        return BundleTestUtils.builder().putBundle("previousBundle", bundle).build();
    }
}