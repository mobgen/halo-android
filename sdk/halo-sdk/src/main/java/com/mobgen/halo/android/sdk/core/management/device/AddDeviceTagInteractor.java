package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

import java.util.List;

/**
 * Interactor to add device tags to the device.
 */
public class AddDeviceTagInteractor implements HaloInteractorExecutor.Interactor<Device> {

    /**
     * Target tags that will be modified.
     */
    private final List<HaloSegmentationTag> mTargetTags;
    /**
     * The device repository.
     */
    private final DeviceRepository mDeviceRepository;
    /**
     * Tells if this synchronizes the device or just waits for it to be synchronized in another moment.
     */
    private final boolean mShouldSendDevice;
    /**
     * Tells if this we should override tags with same key.
     */
    private final boolean mShouldOverrideTags;

    /**
     * Constructor for adding tags.
     *
     * @param deviceRepository The device repository.
     * @param tags             The tags that will be added.
     * @param shouldSendDevice True to send the device. False otherwise.
     */
    public AddDeviceTagInteractor(@NonNull DeviceRepository deviceRepository, @Nullable List<HaloSegmentationTag> tags, boolean shouldSendDevice, boolean shouldOverrideTags) {
        mDeviceRepository = deviceRepository;
        mTargetTags = tags;
        mShouldSendDevice = shouldSendDevice;
        mShouldOverrideTags = shouldOverrideTags;
    }

    @NonNull
    @Override
    public HaloResultV2<Device> executeInteractor() throws Exception {
        mDeviceRepository.addTags(mTargetTags, mShouldOverrideTags);
        if (mShouldSendDevice) {
            return new SendDeviceInteractor(mDeviceRepository).executeInteractor();
        } else {
            return new FetchDeviceInteractor(mDeviceRepository).executeInteractor();
        }
    }
}
