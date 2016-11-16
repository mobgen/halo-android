package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.os.Build;

/**
 * Tag that collects the device model.
 */
public class DeviceModelCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        return HaloSegmentationTag.createDeviceTag("Device Model", Build.MODEL);
    }
}
