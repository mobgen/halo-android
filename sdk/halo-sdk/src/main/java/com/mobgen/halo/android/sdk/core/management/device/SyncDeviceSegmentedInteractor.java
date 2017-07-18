package com.mobgen.halo.android.sdk.core.management.device;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.HaloCore;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

import static com.mobgen.halo.android.sdk.core.management.HaloManagerApi.DEVICE_UPDATE_EVENT_ID;

/**
 * Provides the device information synchronizing it when needed.
 */
public class SyncDeviceSegmentedInteractor implements HaloInteractorExecutor.Interactor<Device> {

    /**
     * The device repository.
     */
    private DeviceRepository mDeviceRepository;
    /**
     * Interacts with the core to add the information requested.
     */
    private HaloCore mCore;

    /**
     * Provides the device information.
     *
     * @param deviceRepository The device repository.
     */
    public SyncDeviceSegmentedInteractor(@NonNull DeviceRepository deviceRepository, @NonNull HaloCore core) {
        mDeviceRepository = deviceRepository;
        mCore = core;
    }

    @NonNull
    @Override
    public HaloResultV2<Device> executeInteractor() {
        HaloStatus.Builder status = HaloStatus.builder();
        Device device = null;
        try {
            device = mDeviceRepository.syncDevice(mCore.segmentationTags());
            //refresh notification token
            Bundle params = new Bundle();
            params.putParcelable("user", device);
            mCore.framework().toolbox().eventHub().emit(new Event(DEVICE_UPDATE_EVENT_ID, params));
        } catch (HaloNetException | HaloParsingException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), device);
    }
}
