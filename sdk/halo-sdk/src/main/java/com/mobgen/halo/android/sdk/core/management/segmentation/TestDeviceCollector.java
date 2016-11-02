package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;

import com.mobgen.halo.android.sdk.core.HaloCore;

/**
 * Tag that marks a given device as a test device. This is usually detected using the BuildConfig field or in our case
 * the development field from {@link HaloCore HaloCore}.
 *
 * @see HaloCore
 * @see TagCollector
 */
public class TestDeviceCollector implements TagCollector {

    /**
     * Checks if it is a test device.
     */
    private boolean mIsTestDevice;

    /**
     * Constructor for the tag as a test device,
     *
     * @param isTestDevice Determines if it is a test device.
     */
    public TestDeviceCollector(boolean isTestDevice) {
        mIsTestDevice = isTestDevice;
    }

    @Override
    public HaloSegmentationTag collect(Context context) {
        return HaloSegmentationTag.createDeviceTag("Test Device", mIsTestDevice);
    }
}
