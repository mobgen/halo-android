package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.os.Build;

/**
 * Tag that collects the version of the OS.
 */
public class PlatformVersionCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        return HaloSegmentationTag.createDeviceTag("Android Version", Build.VERSION.RELEASE);
    }
}
