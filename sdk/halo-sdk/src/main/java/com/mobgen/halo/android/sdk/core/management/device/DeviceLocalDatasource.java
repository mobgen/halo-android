package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.sdk.core.management.models.Device;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Local storage for the halo device.
 */
@Keep
public class DeviceLocalDatasource {

    /**
     * The preferences for the user data.
     */
    private static final String DEVICE_CACHE = "com.mobgen.halo.android.sdk.halo_device";

    /**
     * The storage api.
     */
    private HaloStorageApi mStorageApi;

    /**
     * Constructor for the local data source.
     *
     * @param storageApi The storage api.
     */
    public DeviceLocalDatasource(@NonNull HaloStorageApi storageApi) {
        mStorageApi = storageApi;
    }

    /**
     * Caches the provided device.
     *
     * @param serializedDevice The device.
     * @throws HaloParsingException Error parsing the data.
     */
    public void cacheDevice(@NonNull String serializedDevice) throws HaloParsingException {
        AssertionUtils.notNull(serializedDevice, "device");
        mStorageApi.prefs().edit().putString(DEVICE_CACHE, serializedDevice).commit();
    }

    /**
     * Fetches the cached device as string.
     *
     * @return The cached device as string.
     */
    @Nullable
    public String getCachedDevice() {
        try {
            JSONObject jsonUserData = mStorageApi.prefs().getJsonObject(DEVICE_CACHE, null);
            if (jsonUserData != null) {
                return jsonUserData.toString();
            }
        } catch (JSONException e) {
            Halog.e(getClass(), "The device data cached has a bad format.", e);
        }
        return null;
    }

    /**
     * Clears the current device.
     */
    public void clearCurrentDevice() {
        mStorageApi.prefs().edit().remove(DEVICE_CACHE).commit();
    }
}
