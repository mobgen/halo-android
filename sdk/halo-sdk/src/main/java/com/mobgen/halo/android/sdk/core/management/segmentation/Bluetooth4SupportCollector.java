package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Tag that determines if this device has support for bluetooth 4 protocol.
 */
public class Bluetooth4SupportCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        boolean supportsBluetoothLE = false;
        if (HaloUtils.isAvailableForVersion(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            supportsBluetoothLE = isBLESupported(context);
        }
        return HaloSegmentationTag.createDeviceTag("Bluetooth 4 Support", supportsBluetoothLE);
    }

    /**
     * Checks if BLE is supported.
     * @param context The context.
     * @return True if supported, false otherwise.
     */
    @TargetApi(18)
    private boolean isBLESupported(@NonNull Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
}
