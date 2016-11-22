package com.mobgen.halo.android.social.mock.instrumentation;

import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Device;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;


@Implements(HaloManagerApi.class)
public class HaloManagerApiShadow {

    @Implementation
    public Device getDevice() {
        Device device = new Device("myTestUser","57fb592ff53f3f002aa99d78","email@test.com","notificationtoken");
        return device;
    }

}
