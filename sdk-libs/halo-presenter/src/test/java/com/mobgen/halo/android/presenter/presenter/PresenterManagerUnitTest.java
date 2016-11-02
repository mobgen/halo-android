package com.mobgen.halo.android.presenter.presenter;

import com.mobgen.halo.android.presenter.HaloPresenter;
import com.mobgen.halo.android.presenter.HaloPresenterManager;
import com.mobgen.halo.android.presenter.testing.PresenterTestHelper;
import com.mobgen.halo.android.testing.HaloJUnitTest;
import com.mobgen.halo.android.testing.TestUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@RunWith(BlockJUnit4ClassRunner.class)
public class PresenterManagerUnitTest extends HaloJUnitTest {

    private HaloPresenter mHaloPresenter;
    private HaloPresenterManager mPresenterManager;

    @Before
    public void initialize() {
        mHaloPresenter = mock(HaloPresenter.class);
        mPresenterManager = PresenterTestHelper.lifecycle(mHaloPresenter);
    }

    @Test
    public void thatPresenterLifecycleStatePersists() {
        Assert.assertEquals(HaloPresenterManager.PresenterState.NONE, mPresenterManager.getPresenterState());
        Assert.assertFalse(mPresenterManager.isViewAvailable());
        mPresenterManager.doCreate(null);
        Assert.assertEquals(HaloPresenterManager.PresenterState.CREATED, mPresenterManager.getPresenterState());
        Assert.assertFalse(mPresenterManager.isViewAvailable());

        mPresenterManager.doInit(null);
        Assert.assertEquals(HaloPresenterManager.PresenterState.INIT, mPresenterManager.getPresenterState());
        Assert.assertTrue(mPresenterManager.isViewAvailable());

        mPresenterManager.doLoad();
        Assert.assertEquals(HaloPresenterManager.PresenterState.LOADED, mPresenterManager.getPresenterState());
        Assert.assertTrue(mPresenterManager.isViewAvailable());

        mPresenterManager.doResume();
        Assert.assertEquals(HaloPresenterManager.PresenterState.LOADED, mPresenterManager.getPresenterState());
        Assert.assertTrue(mPresenterManager.isViewAvailable());

        mPresenterManager.doPause();
        Assert.assertEquals(HaloPresenterManager.PresenterState.LOADED, mPresenterManager.getPresenterState());
        Assert.assertTrue(mPresenterManager.isViewAvailable());

        mPresenterManager.doShutdown();
        Assert.assertEquals(HaloPresenterManager.PresenterState.DESTROYED, mPresenterManager.getPresenterState());
        Assert.assertFalse(mPresenterManager.isViewAvailable());

        //Full coverage for state
        TestUtils.shallowEnumCodeCoverage(HaloPresenterManager.PresenterState.class);
    }

    @Test(expected = IllegalStateException.class)
    public void thatPresenterInvalidStateExceptionShutdown() {
        mPresenterManager.doCreate(null);
        mPresenterManager.doShutdown();
        mPresenterManager.doResume();
    }

    @Test(expected = IllegalStateException.class)
    public void thatPresenterInvalidStateExceptionInit() {
        mPresenterManager.doResume();
    }

    @Test
    public void thatOnitializeNoHalo() {
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

    @Test
    public void thatGetPresenter() {
        Assert.assertNull(mPresenterManager.presenter());
        mPresenterManager.doCreate(null);
        Assert.assertNotNull(mPresenterManager.presenter());
    }
}
