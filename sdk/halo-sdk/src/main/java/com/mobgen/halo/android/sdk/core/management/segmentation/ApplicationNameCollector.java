package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;

/**
 * Tag that collects the application name.
 */
public class ApplicationNameCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        if (stringId != 0) {
            String applicationName = context.getString(stringId);
            return HaloSegmentationTag.createDeviceTag("Application Name", applicationName);
        }
        return null;
    }
}
