package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.sdk.BuildConfig;

/**
 * Tag that collects the application version.
 */
public class ApplicationVersionCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        HaloSegmentationTag tag = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = packageInfo.versionName;
            tag = HaloSegmentationTag.createDeviceTag("Application Version", version);
        } catch (PackageManager.NameNotFoundException e) {
            Halog.e(getClass(), "The application version could not be collected for this execution.");
        }
        return tag;
    }
}
