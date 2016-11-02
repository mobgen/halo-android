package com.mobgen.halo.android.presenter.mock.instrumentation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.mobgen.halo.android.presenter.HaloPresenter;
import com.mobgen.halo.android.presenter.PresenterLifeCycleHandler;

import org.mockito.Mockito;

public class MockPresenterActivity extends FragmentActivity implements PresenterLifeCycleHandler<HaloPresenter> {

    @NonNull
    @Override
    public HaloPresenter createPresenter(@Nullable Bundle savedInstaceState) {
        return Mockito.mock(HaloPresenter.class);
    }
}