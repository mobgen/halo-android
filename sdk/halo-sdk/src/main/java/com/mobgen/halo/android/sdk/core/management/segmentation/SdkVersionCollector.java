package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;

import com.mobgen.halo.android.sdk.BuildConfig;

/**
 * Collects the sdk version used for this device.
 */
public class SdkVersionCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        return HaloSegmentationTag.createDeviceTag("Android SDK Version", BuildConfig.HALO_SDK_VERSION);
    }
}
