package com.mobgen.halo.android.presenter.presenter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.mobgen.halo.android.presenter.AbstractHaloPresenter;
import com.mobgen.halo.android.presenter.ConnectionBroadcastReceiver;
import com.mobgen.halo.android.presenter.HaloPresenterManager;
import com.mobgen.halo.android.presenter.HaloViewTranslator;
import com.mobgen.halo.android.presenter.PresenterLifeCycleHandler;
import com.mobgen.halo.android.testing.BundleTestUtils;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractPresenterTest extends HaloRobolectricTest {

    private MockHaloPresenter mPresenter;
    private HaloViewTranslator mView;
    private Bundle mBundle;
    private HaloPresenterManager mPresenterManager;

    @SuppressWarnings("unchecked")
    @Override
    public void onStart() throws Exception {
        mView = mock(HaloViewTranslator.class);
        mPresenter = new MockHaloPresenter(mView);
        mBundle = BundleTestUtils.builder().putString("mock", "mock").build();
        PresenterLifeCycleHandler handler = mock(PresenterLifeCycleHandler.class);
        when(handler.createPresenter(mBundle)).thenReturn(mPresenter);
        when(handler.getSupportFragmentManager()).thenReturn(mock(FragmentManager.class));
        mPresenterManager = new HaloPresenterManager<>(handler);
    }

    @Test
    public void thatPresenterCallbacksLifecycleAreCalled() {
        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(mView.getContext()).thenReturn(context);

        mPresenterManager.doCreate(mBundle);
        Assert.assertFalse(mPresenter.mInit);
        mPresenterManager.doInit(mBundle);
        Assert.assertTrue(mPresenter.mInit);
        Assert.assertFalse(mPresenter.mInitialized);
        mPresenterManager.doLoad();
        Assert.assertTrue(mPresenter.mInitialized);
        Assert.assertFalse(mPresenter.mResumed);
        mPresenterManager.doResume();
        Assert.assertTrue(mPresenter.mResumed);
        Assert.assertFalse(mPresenter.mPaused);
        mPresenterManager.doPause();
        Assert.assertTrue(mPresenter.mPaused);
        Assert.assertFalse(mPresenter.mShutdown);
        mPresenterManager.doShutdown();
        Assert.assertTrue(mPresenter.mShutdown);
    }

    @Test
    public void thatSavedInstanceStatePersists() {
        Bundle auxBundle = mPresenter.onSaveInstanceState(mBundle);
        Assert.assertNotNull(auxBundle.getBundle("previousBundle"));
        Assert.assertEquals(mBundle, auxBundle.getBundle("previousBundle"));
    }

    @Test
    public void thatGetPresenterGivesPresenter() {
        Assert.assertNotNull(mPresenter.getView());
    }

    @Test
    public void thatNetworkConnectedIsNotified() {
        //Setup connectivity and context
        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo info = mock(NetworkInfo.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(info);
        when(info.isConnected()).thenReturn(true);
        when(info.isAvailable()).thenReturn(true);
        when(mView.getContext()).thenReturn(context);
        when(mView.isViewAvailable()).thenReturn(true);

        mPresenterManager.doSetup(mBundle);
        final boolean[] test = new boolean[1];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                test[0] = true;
                return null;
            }
        }).when(mView).onNetworkConnected();

        Assert.assertNotNull(mPresenter.getNetworkReceiver());
        mPresenter.getNetworkReceiver().onReceive(context, null);
        Assert.assertTrue(test[0]);
        Assert.assertTrue(mPresenter.mConnected);
    }

    @Test
    public void thatNetworkDisconnectedIsNotified() {
        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(mView.getContext()).thenReturn(context);

        mPresenterManager.doSetup(mBundle);
        final boolean[] test = new boolean[1];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                test[0] = true;
                return null;
            }
        }).when(mView).onNetworkLost();


        Assert.assertNotNull(mPresenter.getNetworkReceiver());
        mPresenter.getNetworkReceiver().onReceive(context, null);
        Assert.assertTrue(test[0]);
        Assert.assertFalse(mPresenter.mConnected);
    }

    @Test
    public void thatAvoidReceiverRecreationWorks() {
        Context context = mock(Context.class);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        when(context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(mView.getContext()).thenReturn(context);

        mPresenterManager.doSetup(mBundle);
        ConnectionBroadcastReceiver receiver = mPresenter.getNetworkReceiver();
        mPresenterManager.doInit(mBundle);
        Assert.assertEquals(receiver, mPresenter.getNetworkReceiver());
    }

    @Test
    public void thatShutdownBeforeDoesNotInitReceiver() {
        Assert.assertNull(mPresenter.getNetworkReceiver());
        mPresenterManager.doCreate(mBundle);
        Assert.assertNull(mPresenter.getNetworkReceiver());
        mPresenterManager.doShutdown();
        Assert.assertNull(mPresenter.getNetworkReceiver());
    }

    private class MockHaloPresenter extends AbstractHaloPresenter {
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
}
