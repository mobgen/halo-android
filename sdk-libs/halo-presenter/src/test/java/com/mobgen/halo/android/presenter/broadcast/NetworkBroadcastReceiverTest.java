package com.mobgen.halo.android.presenter.broadcast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mobgen.halo.android.presenter.BuildConfig;
import com.mobgen.halo.android.presenter.ConnectionBroadcastReceiver;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkBroadcastReceiverTest {

    @Test
    public void broadcastNotificationTest() {
        final boolean[] test = new boolean[1];
        ConnectionBroadcastReceiver receiver = new ConnectionBroadcastReceiver(new ConnectionBroadcastReceiver.NetworkStateListener() {
            @Override
            public void onNetworkStateChangedTo(boolean connected) {
                test[0] = connected;
            }
        });
        Context mockContext = mock(Context.class);
        ConnectivityManager manager = mock(ConnectivityManager.class);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);

        //Check with connectivity
        receiver.onReceive(mockContext, null);
        Assert.assertFalse(test[0]);

        //Check without connectivity
        NetworkInfo info = mock(NetworkInfo.class);
        when(info.isAvailable()).thenReturn(true);
        when(info.isConnected()).thenReturn(true);
        when(manager.getActiveNetworkInfo()).thenReturn(info);
        receiver.onReceive(mockContext, null);
        Assert.assertTrue(test[0]);
    }
}
