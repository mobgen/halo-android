package com.mobgen.halo.android.presenter.presenter;

import com.mobgen.halo.android.presenter.HaloPresenter;
import com.mobgen.halo.android.presenter.HaloPresenterManager;
import com.mobgen.halo.android.presenter.testing.PresenterTestHelper;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.mobgen.halo.android.presenter.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class PresenterManagerIntegrationTest extends HaloRobolectricTest {

    private HaloPresenter mHaloPresenter;
    private HaloPresenterManager mPresenterManager;
    private Halo mHalo;

    @Override
    public void onStart() throws Exception {
        mHaloPresenter = mock(HaloPresenter.class);
        mPresenterManager = PresenterTestHelper.lifecycle(mHaloPresenter);
        mHalo = givenADefaultHalo("");
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
    }

    @Test
    public void initializeHaloTest() {
        final boolean[] initialized = new boolean[1];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                initialized[0] = true;
                return null;
            }
        }).when(mHaloPresenter).onInitialized();
        mPresenterManager.doSetup(null);
        Assert.assertTrue(initialized[0]);
    }
}
