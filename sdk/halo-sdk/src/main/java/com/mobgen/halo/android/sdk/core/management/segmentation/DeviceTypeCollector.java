package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.sdk.R;

/**
 * Tag that collects the type of device. The current available types are Tablet and Phone.
 */
public class DeviceTypeCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        String deviceType;
        if (isTablet(context)) {
            deviceType = "Tablet";
        } else {
            deviceType = "Phone";
        }
        return HaloSegmentationTag.createDeviceTag("Device Type", deviceType);
    }

    /**
     * Determines if a device is a tablet or not.
     *
     * @param ctx The context for the device.
     * @return True if the device is a tablet, false otherwise.
     */
    @Api(1.0)
    private static boolean isTablet(Context ctx) {
        return ctx.getResources().getBoolean(R.bool.isTablet);
    }
}
