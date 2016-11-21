package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.HaloCore;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Sends the in memory device to the server.
 */
public class SendDeviceInteractor implements HaloInteractorExecutor.Interactor<Device> {

    /**
     * The device repository.
     */
    private DeviceRepository mDeviceRepository;

    /**
     * Provides the device information.
     *
     * @param deviceRepository The device repository.
     */
    public SendDeviceInteractor(@NonNull DeviceRepository deviceRepository) {
        mDeviceRepository = deviceRepository;
    }

    @NonNull
    @Override
    public HaloResultV2<Device> executeInteractor() {
        HaloStatus.Builder status = HaloStatus.builder();
        Device device = null;
        try {
            device = mDeviceRepository.sendDevice();
        } catch (HaloNetException | HaloParsingException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), device);
    }
}
