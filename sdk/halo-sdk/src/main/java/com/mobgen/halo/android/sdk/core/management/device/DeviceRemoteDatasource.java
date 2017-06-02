package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.models.Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Remote data source for the devices.
 */
public class DeviceRemoteDatasource {

    /**
     * Requests the creation of a new device.
     */
    private static final String URL_UPDATE_DEVICE = "api/segmentation/appuser/{deviceId}";
    /**
     * Updates the device created.
     */
    private static final String URL_CREATE_DEVICE = "api/segmentation/appuser/";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Creates a remote data source for the device.
     *
     * @param clientApi The remote data source.
     */
    public DeviceRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Updates the device with a previous device or creates a new one if no device
     * is available.
     *
     * @param previousDevice The previous device.
     * @return The device.
     */
    @NonNull
    public Device updateDevice(@NonNull Device previousDevice) throws HaloNetException {
        AssertionUtils.notNull(previousDevice, "device");
        Device finalDevice;
        if (previousDevice.isAnonymous()) {
            finalDevice = createDevice(previousDevice);
        } else {
            finalDevice = updateDeviceRequest(previousDevice);
        }
        return finalDevice;
    }

    /**
     * Syncs the device with a create request.
     *
     * @param device The device that will be synced.
     * @return The device created.
     * @throws HaloNetException Exception while performing the request.
     */
    @NonNull
    private Device createDevice(@NonNull Device device) throws HaloNetException {
        try {
            //Do the request
            return HaloRequest.builder(mClientApi)
                    .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_CREATE_DEVICE)
                    .method(HaloRequestMethod.POST)
                    .body(HaloBodyFactory.jsonObjectBody(new JSONObject(Device.serialize(device, mClientApi.framework().parser()))))
                    .build().execute(Device.class);
        } catch (JSONException | HaloParsingException e) {
            throw new HaloNetParseException("The device object is not serializable to a JSONObject format.", e);
        }
    }

    /**
     * Syncs with an update call.
     *
     * @param device The device that will be synced.
     * @return The device returned.
     * @throws HaloNetException Exception produced while syncing.
     */
    @NonNull
    private Device updateDeviceRequest(@NonNull Device device) throws HaloNetException {
        Map<String, String> params = new HashMap<>(1);
        params.put("deviceId", device.getId());
        try {
            //Do the request
            return HaloRequest.builder(mClientApi)
                    .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_UPDATE_DEVICE, params)
                    .method(HaloRequestMethod.PUT)
                    .body(HaloBodyFactory.jsonObjectBody(new JSONObject(Device.serialize(device, mClientApi.framework().parser()))))
                    .build().execute(Device.class);
        } catch (JSONException | HaloParsingException e) {
            throw new HaloNetParseException("The device object is not serializable to a JSONObject format.", e);
        }
    }

    /**
     * Provides the device from the API.
     *
     * @param device The device to retrieve.
     * @return The device brought.
     */
    @NonNull
    public Device getDevice(@NonNull Device device) throws HaloNetException {
        AssertionUtils.notNull(device, "device");
        if (device.isAnonymous()) {
            return device;
        }
        Map<String, String> params = new HashMap<>(1);
        params.put("deviceId", device.getId());
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_UPDATE_DEVICE, params)
                .method(HaloRequestMethod.GET)
                .build().execute(Device.class);
    }
}
