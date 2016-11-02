package com.mobgen.halo.android.presenter.testing;

import android.support.v4.app.FragmentManager;

import com.mobgen.halo.android.presenter.HaloPresenter;
import com.mobgen.halo.android.presenter.HaloPresenterManager;
import com.mobgen.halo.android.presenter.PresenterLifeCycleHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PresenterTestHelper {

    /**
     * Mocks a presenter of the given class and provides the presenter manger.
     *
     * @param presenter The presenter instance.
     * @return The Presenter manager that will lead the lifecycle of the presenter.
     */
    @SuppressWarnings("unchecked")
    public static HaloPresenterManager lifecycle(HaloPresenter presenter) {
        PresenterLifeCycleHandler presenterLifecycle = mock(PresenterLifeCycleHandler.class);
        when(presenterLifecycle.createPresenter(null)).thenReturn(presenter);
        FragmentManager fragmentManager = mock(FragmentManager.class);
        when(presenterLifecycle.getSupportFragmentManager()).thenReturn(fragmentManager);

        return new HaloPresenterManager<>(presenterLifecycle);
    }
}
