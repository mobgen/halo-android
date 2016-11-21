package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.HaloCore;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Interactor that sets the notification token into the device being respectfull with the operations done into this device.
 */
public class SetNotificationTokenInteractor implements HaloInteractorExecutor.Interactor<Device>{

    /**
     * The device repository.
     */
    private DeviceRepository mDeviceRepository;
    /**
     * The notification token that will be set.
     */
    private String mNotificationToken;

    /**
     * Provides the device information.
     *
     * @param deviceRepository The device repository.
     */
    public SetNotificationTokenInteractor(@NonNull DeviceRepository deviceRepository, @Nullable String notificationToken) {
        mDeviceRepository = deviceRepository;
        mNotificationToken = notificationToken;
    }

    @NonNull
    @Override
    public HaloResultV2<Device> executeInteractor() throws Exception {
        boolean changed = mDeviceRepository.pushNotificationToken(mNotificationToken);
        if(changed) {
            return new SendDeviceInteractor(mDeviceRepository).executeInteractor();
        }else{
            return new FetchDeviceInteractor(mDeviceRepository).executeInteractor();
        }
    }
}
