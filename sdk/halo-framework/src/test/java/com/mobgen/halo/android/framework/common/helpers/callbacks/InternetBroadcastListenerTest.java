package com.mobgen.halo.android.framework.common.helpers.callbacks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mobgen.halo.android.framework.BuildConfig;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static android.R.attr.permission;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InternetBroadcastListenerTest extends HaloRobolectricTest {

    private HaloFramework mFramework;
    private CallbackFlag mCallbackFlag;
    private Context mContext;

    @Before
    public void initialize() {
        mFramework = FrameworkMock.createSameThreadFramework("myTestEndpoint");
        mCallbackFlag = newCallbackFlag();
        givenInternetPermission();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCreateInternetListener(){
        InternetBroadcastListener listener = InternetBroadcastListener.listen(RuntimeEnvironment.application,givenAInternetBroadcastListener(mCallbackFlag));
        assertThat(listener).isNotNull();
        listener.unlisten(listener);
    }

    @Test
    public void thatInternetListenerIsReadyCheck(){
        InternetBroadcastListener listener = InternetBroadcastListener.listen(mContext,givenAInternetBroadcastListener(mCallbackFlag));
        listener.onReceive(mContext,new Intent());
        listener.unlisten();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    public static InternetBroadcastListener.InternetListener givenAInternetBroadcastListener(final CallbackFlag flag){
        return  new InternetBroadcastListener.InternetListener() {
            @Override
            public void onInternetReady() {
                flag.flagExecuted();
            }
        };
    }

    public void givenInternetPermission(){
        mContext = mock(Context.class);
        when(mContext.checkPermission(eq("ACCESS_NETWORK_STATE"),anyInt(),anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        final ConnectivityManager manager = mock(ConnectivityManager.class);
        final NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.isAvailable()).thenReturn(true);
        when(networkInfo.isConnected()).thenReturn(true);
    }
}
