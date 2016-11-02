package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;

import com.mobgen.halo.android.sdk.BuildConfig;

/**
 * Tag that provides the platform name. In this case the hardcoded name is Android since this
 * is the Android SDK for HALO.
 */
public class PlatformNameCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        return HaloSegmentationTag.createDeviceTag("Platform Name", BuildConfig.HALO_PLATFORM_NAME);
    }
}
