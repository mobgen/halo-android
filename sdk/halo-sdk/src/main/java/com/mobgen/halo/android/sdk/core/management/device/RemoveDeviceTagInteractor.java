package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

import java.util.List;

/**
 * Interactor to remove device tags from the device.
 */
public class RemoveDeviceTagInteractor implements HaloInteractorExecutor.Interactor<Device> {

    /**
     * Target tags that will be removed.
     */
    private final List<String> mTargetTags;
    /**
     * The device repository.
     */
    private final DeviceRepository mDeviceRepository;
    /**
     * Tells if this synchronizes the device or just waits for it to be synchronized in another moment.
     */
    private final boolean mShouldSendDevice;

    /**
     * Constructor for removing tags.
     * @param deviceRepository The device repository.
     * @param tags The tags that will be removed.
     * @param shouldSendDevice True to send the device. False otherwise.
     */
    public RemoveDeviceTagInteractor(@NonNull DeviceRepository deviceRepository, @Nullable List<String> tags, boolean shouldSendDevice) {
        mDeviceRepository = deviceRepository;
        mTargetTags = tags;
        mShouldSendDevice = shouldSendDevice;
    }

    @NonNull
    @Override
    public HaloResultV2<Device> executeInteractor() throws Exception {
        mDeviceRepository.removeTags(mTargetTags);
        if (mShouldSendDevice) {
            return new SendDeviceInteractor(mDeviceRepository).executeInteractor();
        } else {
            return new FetchDeviceInteractor(mDeviceRepository).executeInteractor();
        }
    }
}
