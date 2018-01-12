package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.mobgen.halo.android.sdk.BuildConfig;
import com.mobgen.halo.android.sdk.R;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Locale;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 23)
public class SegmentationTagCollectorsTests extends HaloRobolectricTest {

    @Test
    public void thatLoadFromDefaultLocale() {
        Locale.setDefault(Locale.CANADA);
        String locale = HaloLocale.fromDefaultLocale();
        assertThat(locale).isNotNull();
    }

    @Test
    public void thatLoadFromLocale() {
        String locale = HaloLocale.fromLocale(Locale.CANADA_FRENCH);
        assertThat(locale).isEqualTo(HaloLocale.FRENCH_CANADA);
    }

    @Test
    public void thatApplicationNameTagIsOk() {
        ApplicationNameCollector collector = new ApplicationNameCollector();
        ApplicationInfo info = new ApplicationInfo();
        info.labelRes = R.string.sdkName;
        Application app = spy(RuntimeEnvironment.application);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return "Mock app name";
            }
        }).when(app).getString(R.string.sdkName);
        when(app.getApplicationInfo()).thenReturn(info);
        HaloSegmentationTag tag = collector.collect(app);
        assertThat(tag.getValue()).isEqualTo("Mock app name");
        assertThat(tag.getName()).isEqualTo("Application Name");
    }

    @Test
    public void thatApplicationSdkVersionTagIsOk() {
        SdkVersionCollector collector = new SdkVersionCollector();
        HaloSegmentationTag tag = collector.collect(RuntimeEnvironment.application);
        assertThat(tag.getValue()).isEqualTo(BuildConfig.HALO_SDK_VERSION);
        assertThat(tag.getName()).isEqualTo("Android SDK Version");
    }

    @Test
    public void thatApplicationVersionTagIsOk() {
        ApplicationVersionCollector collector = new ApplicationVersionCollector();
        HaloSegmentationTag tag = collector.collect(RuntimeEnvironment.application);
        //  assertThat(tag.getValue()).isEqualTo(BuildConfig.VERSION_NAME);
        assertThat(tag.getName()).isEqualTo("Application Version");
    }

    @Test
    public void thatApplicationVersionTagCreateAnException() throws PackageManager.NameNotFoundException {
        Context context = mock(Context.class);
        PackageManager manager = mock(PackageManager.class);
        String mockPackageName = "com.mock.mockpackage";
        when(manager.getPackageInfo(mockPackageName, 0)).thenThrow(new PackageManager.NameNotFoundException("Name not found"));
        when(context.getPackageManager()).thenReturn(manager);
        when(context.getPackageName()).thenReturn(mockPackageName);
        ApplicationVersionCollector collector = new ApplicationVersionCollector();
        HaloSegmentationTag tag = collector.collect(context);
        assertThat(tag).isNull();
    }

    @Test
    public void thatBluetooth4SupportIsEnabled() {
        Bluetooth4SupportCollector bluetoothTag = new Bluetooth4SupportCollector();
        Context context = mock(Context.class);
        PackageManager manager = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(manager);
        when(manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)).thenReturn(true);
        assertThat(bluetoothTag.collect(context)).isNotNull();
    }

    @Test
    public void thatBluetooth4SupportTagIsDisabled() {
        Bluetooth4SupportCollector bluetoothTag = new Bluetooth4SupportCollector();
        Context context = mock(Context.class);
        PackageManager manager = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(manager);
        when(manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)).thenReturn(false);
        assertThat(Boolean.valueOf(bluetoothTag.collect(context).getValue())).isFalse();
    }

    @Test
    public void thatDeviceManufacturerTagIsOk() {
        DeviceManufacturerCollector manufacturer = new DeviceManufacturerCollector();
        assertThat(manufacturer.collect(mock(Context.class)).getValue()).isEqualTo("unknown");
    }

    @Test
    public void thatDeviceModelTagIsOk() {
        DeviceModelCollector model = new DeviceModelCollector();
        assertThat(model.collect(mock(Context.class)).getValue()).isEqualTo("unknown");
    }

    @Test
    public void thatDeviceTypeTagIsOk() {
        String tablet = "Tablet";
        String phone = "Phone";
        DeviceTypeCollector type = new DeviceTypeCollector();
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);
        when(context.getResources()).thenReturn(resources);
        when(resources.getBoolean(R.bool.isTablet)).thenReturn(true);
        assertThat(type.collect(context).getValue()).isEqualTo(tablet);
        when(resources.getBoolean(R.bool.isTablet)).thenReturn(false);
        assertThat(type.collect(context).getValue()).isEqualTo(phone);
    }

    @Test
    public void thatNfcSupportTagIsOk() {
        NFCSupportCollector nfcTag = new NFCSupportCollector();
        Context context = mock(Context.class);
        NfcManager manager = mock(NfcManager.class);
        Context emptyContext = mock(Context.class);
        NfcManager emptyManager = mock(NfcManager.class);
        when(context.getSystemService(Context.NFC_SERVICE)).thenReturn(manager);
        when(manager.getDefaultAdapter()).thenReturn(mock(NfcAdapter.class));
        when(emptyContext.getSystemService(Context.NFC_SERVICE)).thenReturn(emptyManager);
        when(emptyManager.getDefaultAdapter()).thenReturn(null);
        assertThat(Boolean.valueOf(nfcTag.collect(emptyContext).getValue())).isFalse();
        assertThat(nfcTag.collect(context)).isNotNull();
    }

    @Test
    public void thatPlatformNameTagIsOk() {
        assertThat(new PlatformNameCollector().collect(null).getValue()).isEqualTo(BuildConfig.HALO_PLATFORM_NAME);
        assertThat(new PlatformNameCollector().collect(mock(Context.class)).getValue()).isEqualTo(BuildConfig.HALO_PLATFORM_NAME);
    }

    @Test
    public void thatPlatformVersionTagIsOk() {
        assertThat(new PlatformVersionCollector().collect(null).getValue()).isEqualTo(Build.VERSION.RELEASE);
        assertThat(new PlatformVersionCollector().collect(mock(Context.class)).getValue()).isEqualTo(Build.VERSION.RELEASE);
    }

    @Test
    public void thatScreenSizeTagIsOk() {
        Context context = mock(Context.class);
        WindowManager manager = mock(WindowManager.class);
        Display display = mock(Display.class);
        when(context.getSystemService(Context.WINDOW_SERVICE)).thenReturn(manager);
        when(manager.getDefaultDisplay()).thenReturn(display);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Point point = (Point) invocation.getArguments()[0];
                point.x = 1080;
                point.y = 720;
                return null;
            }
        }).when(display).getSize(new Point());
        assertThat(new ScreenSizeCollector().collect(context).getValue()).isEqualTo("1080x720");
    }

    @Test
    public void thatTestDeviceTagValueIsOk() {
        TestDeviceCollector collector = new TestDeviceCollector(true);
        HaloSegmentationTag tag = collector.collect(RuntimeEnvironment.application);
        collector = new TestDeviceCollector(false);
        tag = collector.collect(RuntimeEnvironment.application);
        assertThat(Boolean.valueOf(tag.getValue())).isFalse();
    }

    @Test
    public void thatEqualsSegmentationIsOk() {
        TestDeviceCollector testCollector = new TestDeviceCollector(true);
        HaloSegmentationTag tag = testCollector.collect(RuntimeEnvironment.application);
        HaloSegmentationTag secondTag = testCollector.collect(RuntimeEnvironment.application);
        assertThat(tag.equals(secondTag)).isTrue();
        assertThat(tag.equals("Fake")).isFalse();
        assertThat(tag.getName().hashCode()).isEqualTo(tag.hashCode());
        assertThat(tag.compareTo(secondTag)).isEqualTo(0);
    }
}
