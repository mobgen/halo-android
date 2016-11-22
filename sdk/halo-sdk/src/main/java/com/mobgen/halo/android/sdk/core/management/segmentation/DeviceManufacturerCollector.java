package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.os.Build;

/**
 * Tag that collects the device manufacturer name.
 */
public class DeviceManufacturerCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        return HaloSegmentationTag.createDeviceTag("Device Manufacturer", Build.MANUFACTURER);
    }
}
