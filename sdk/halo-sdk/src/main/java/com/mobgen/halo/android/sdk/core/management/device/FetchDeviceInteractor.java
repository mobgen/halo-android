package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.HaloCore;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Fetches the current device into core.
 */
public class FetchDeviceInteractor implements HaloInteractorExecutor.Interactor<Device> {

    /**
     * Device repository.
     */
    private DeviceRepository mDeviceRepository;

    /**
     * Constructor for the interactor.
     * @param deviceRepository The device interactor.
     */
    public FetchDeviceInteractor(@NonNull DeviceRepository deviceRepository) {
        AssertionUtils.notNull(deviceRepository, "deviceRepository");
        mDeviceRepository = deviceRepository;
    }

    @NonNull
    @Override
    public HaloResultV2<Device> executeInteractor() throws Exception {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        return new HaloResultV2<>(status.build(), mDeviceRepository.getCachedDevice());
    }
}
