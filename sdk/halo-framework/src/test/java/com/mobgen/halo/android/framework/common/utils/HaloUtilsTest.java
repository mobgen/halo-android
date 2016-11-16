package com.mobgen.halo.android.framework.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HaloUtilsTest extends HaloRobolectricTest {

    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private CallbackFlag mCallbackFlag;

    @Before
    public void initialize() {
        mContext = mock(Context.class);
        mConnectivityManager = mock(ConnectivityManager.class);
        when(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager);
        mCallbackFlag = newCallbackFlag();
    }

    @Test
    public void thatConnectionCheckNoPermission() {
        when(mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_DENIED);
        Assert.assertFalse(HaloUtils.isNetworkConnected(mContext));
    }

    @Test
    public void thatConnectionCheckNoConnectionUnavailable() {
        when(mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
        NetworkInfo info = mock(NetworkInfo.class);
        when(info.isAvailable()).thenReturn(false);
        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(info);
        Assert.assertFalse(HaloUtils.isNetworkConnected(mContext));
    }

    @Test
    public void thatConnectionCheckNoConnectionDisconnected() {
        when(mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
        NetworkInfo info = mock(NetworkInfo.class);
        when(info.isAvailable()).thenReturn(true);
        when(info.isConnected()).thenReturn(false);
        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(info);
        Assert.assertFalse(HaloUtils.isNetworkConnected(mContext));
    }

    @Test
    public void thatConnectionCheckDisconnectedNull() {
        when(mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
        Assert.assertFalse(HaloUtils.isNetworkConnected(mContext));
    }

    @Test
    public void thatConnectionCheckConnected() {
        when(mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo info = mock(NetworkInfo.class);
        when(info.isAvailable()).thenReturn(true);
        when(info.isConnected()).thenReturn(true);
        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(info);
        Assert.assertTrue(HaloUtils.isNetworkConnected(mContext));
    }

    @Test
    public void thatIsChargingCheckIsTrue() {
        Intent intent = new Intent().putExtra(BatteryManager.EXTRA_PLUGGED, 1);
        assertThat(HaloUtils.isCharging(mContext, intent)).isTrue();
    }

    @Test
    public void thatIsChargingCheckIsFalswe() {
        Intent intent = new Intent().putExtra(BatteryManager.EXTRA_PLUGGED, -1);
        assertThat(HaloUtils.isCharging(mContext, intent)).isFalse();
    }

    @Test
    public void thatPostToUI() {
        HaloUtils.postUI(new Runnable() {
            @Override
            public void run() {
                mCallbackFlag.flagExecuted();
            }
        });
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatSha1CreateHash() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String textToHash = "HaloNetClientTest";
        assertThat(HaloUtils.sha1(textToHash)).isEqualTo(HaloUtils.sha1("HaloNetClientTest"));
    }

    @Test
    public void thatIsAValidColor(){
        assertThat(HaloUtils.isColor("#000000")).isTrue();
    }

    @Test
    public void thatIsNotAValidColor(){
        assertThat(HaloUtils.isColor("#zzzzzz")).isFalse();
    }

    @Test
    public void thatIsAvailableForVersion(){
        assertThat(HaloUtils.isAvailableForVersion(1)).isTrue();
    }

    @Test
    public void thatIsNotAvailableForVersion(){
        assertThat(HaloUtils.isAvailableForVersion(Build.VERSION.SDK_INT+1)).isFalse();
    }

    @Test
    public void thatHideSoftwareKeyboard(){
        View view =  mock(View.class);
        InputMethodManager inputMethodManager = mock(InputMethodManager.class);
        when(view.getContext()).thenReturn(mContext);
        when(mContext.getSystemService(Activity.INPUT_METHOD_SERVICE)).thenReturn(inputMethodManager);
        when(inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)).thenReturn(true);
        HaloUtils.hideKeyboard(view);
        assertThat(inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)).isTrue();
    }

    @Test
    public void thatCanGetDrawable(){
        Resources resources = mock(Resources.class);
        Drawable drawable = mock(Drawable.class);
        when(mContext.getResources()).thenReturn(resources);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            when(resources.getDrawable(android.R.drawable.alert_dark_frame,null)).thenReturn(drawable);
        }
        else{
            when(resources.getDrawable(android.R.drawable.alert_dark_frame)).thenReturn(drawable);
        }
        assertThat(HaloUtils.getDrawable(mContext, android.R.drawable.alert_dark_frame)).isNotNull();
        assertThat(HaloUtils.getDrawable(mContext, android.R.drawable.alert_dark_frame)).isEqualTo(drawable);
    }

    @Test
    public void thatHaveAFlag(){
        assertThat(HaloUtils.hasFlag(1,1)).isTrue();
    }

    @Test
    public void thatnotHaveAFlag(){
        assertThat(HaloUtils.hasFlag(1,2)).isFalse();
    }

    @Test
    public void thatConvertsByteArrayToString(){
        byte [] bytes = "TestString".getBytes();
        assertThat(HaloUtils.bytesToHex(bytes).toLowerCase()).isEqualTo(HaloUtils.convertToHex(bytes).toLowerCase());
    }

    @Test
    public void thatConvertsHexToBytes(){
        byte [] bytes = "TestString".getBytes();
        assertThat(HaloUtils.hexToBytes(HaloUtils.convertToHex(bytes))).isEqualTo(bytes);
    }

}