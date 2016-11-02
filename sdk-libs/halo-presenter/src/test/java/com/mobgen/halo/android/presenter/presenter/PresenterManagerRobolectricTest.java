package com.mobgen.halo.android.presenter.presenter;

import android.os.Bundle;

import com.mobgen.halo.android.presenter.HaloPresenter;
import com.mobgen.halo.android.presenter.HaloPresenterManager;
import com.mobgen.halo.android.presenter.mock.instrumentation.MockPresenterActivity;
import com.mobgen.halo.android.testing.BundleTestUtils;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PresenterManagerRobolectricTest extends HaloRobolectricTest {

    private Bundle mBundle;

    @Before
    public void initialize() {
        mBundle = BundleTestUtils.builder().putString("mock", "mock string").build();
    }

    @Test
    public void thatSaveInstancePresenterIsKept() {
        MockPresenterActivity activity = Robolectric.setupActivity(MockPresenterActivity.class);
        HaloPresenterManager<HaloPresenter> manager = new HaloPresenterManager<>(activity);
        assertThat(manager.getPresenter()).isNull();
        manager.doSetup(mBundle);
        assertThat(manager.getPresenter()).isNotNull();

        //Kill this instance
        manager.doPause();
        manager.doSaveInstance(mBundle);
        assertThat(manager.getPresenter()).isNotNull();
        manager.doShutdown();

        //Recreate to get the cached presenter
        HaloPresenter previousPresenter = manager.presenter();
        activity.recreate();
        manager.doSetup(mBundle);
        assertThat(manager.getPresenter()).isNotNull();
        assertThat(manager.getPresenter()).isEqualTo(previousPresenter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void thatRemovesThePresenterOnFinished() {
        MockPresenterActivity activity = Robolectric.setupActivity(MockPresenterActivity.class);
        HaloPresenterManager<HaloPresenter> manager = new HaloPresenterManager<>(activity);
        manager.doSetup(mBundle);
        manager.doPause();
        manager.doSaveInstance(mBundle);
        manager.doShutdown();
        manager.doSetup(mBundle);
        manager.doSaveInstance(mBundle);
        assertThat(manager.getPresenter()).isNotNull();
    }
}
