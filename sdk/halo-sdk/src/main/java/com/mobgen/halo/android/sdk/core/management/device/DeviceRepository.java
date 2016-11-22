package com.mobgen.halo.android.sdk.core.management.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNotFoundException;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import java.util.List;

/**
 * The device repository that interacts with all the device elements.
 */
public class DeviceRepository {

    /**
     * The parser.
     */
    private Parser.Factory mParser;
    /**
     * The device local data source.
     */
    private DeviceLocalDatasource mDeviceLocalDatasource;
    /**
     * The device remote data source.
     */
    private DeviceRemoteDatasource mDeviceRemoteDatasource;
    /**
     * The cached device unique instance.
     */
    private Device mCachedDevice;

    /**
     * Constructor for the repository.
     *
     * @param deviceLocalDatasource  The local repository.
     * @param deviceRemoteDatasource The remote repository.
     */
    public DeviceRepository(@NonNull Parser.Factory parser, @NonNull DeviceRemoteDatasource deviceRemoteDatasource, @NonNull DeviceLocalDatasource deviceLocalDatasource) {
        AssertionUtils.notNull(parser, "parser");
        AssertionUtils.notNull(deviceRemoteDatasource, "deviceRemoteDatasource");
        AssertionUtils.notNull(deviceLocalDatasource, "deviceLocalDatasource");
        mParser = parser;
        mDeviceRemoteDatasource = deviceRemoteDatasource;
        mDeviceLocalDatasource = deviceLocalDatasource;
    }

    /**
     * Syncs the current device with the one in the cloud. Intended to be called on startup.
     *
     * @param tags The tags.
     * @return The device synchronized.
     * @throws HaloNetException     Error while performing the network request.
     * @throws HaloParsingException Error while serializing the device.
     */
    @NonNull
    public synchronized Device syncDevice(@NonNull List<HaloSegmentationTag> tags) throws HaloNetException, HaloParsingException {
        AssertionUtils.notNull(tags, "tags");
        mCachedDevice = getCachedDevice();
        if (!mCachedDevice.isAnonymous()) {
            try {
                mCachedDevice = mDeviceRemoteDatasource.getDevice(mCachedDevice);
            }catch (HaloNotFoundException e) {
                Halog.w(getClass(), "There is a cached device that is not present in the server. Creating a new one");
                Halog.e(getClass(), "Creating new device", e);
                clearCachedDevice();
                return syncDevice(tags);
            }
        }
        mCachedDevice.addTags(tags);
        return sendDevice();
    }

    /**
     * Updates the device with the one in the server.
     *
     * @return The device returned.
     * @throws HaloNetException     Network exception.
     * @throws HaloParsingException Parsing exception.
     */
    @NonNull
    public synchronized Device sendDevice() throws HaloParsingException, HaloNetException {
        mCachedDevice = getCachedDevice();
        try {
            mCachedDevice = mDeviceRemoteDatasource.updateDevice(mCachedDevice);
        }catch (HaloNotFoundException e){
            Halog.w(getClass(), "There is a cached device that is not present in the server. Creating a new one");
            Halog.e(getClass(), "Making the device anonymous", e);
            mCachedDevice.makeAnonymous();
            return sendDevice();
        }
        mDeviceLocalDatasource.cacheDevice(Device.serialize(mCachedDevice, mParser));
        return mCachedDevice;
    }

    /**
     * Provides the cached device. If in memory this one is brought. Otherwise, the new device will be provided.
     *
     * @return The device.
     */
    @NonNull
    public synchronized Device getCachedDevice() {
        if (mCachedDevice == null) {
            mCachedDevice = getAlwaysDevice();
        }
        return mCachedDevice;
    }

    /**
     * Provides the in memory device. This is the only device that can be gotten from the memory.
     *
     * @return The device provided.
     */
    @Nullable
    public Device getDeviceInMemory() {
        return mCachedDevice;
    }

    /**
     * Sets in a synchronized way the notifications token into the current device.
     *
     * @param notificationToken The notifications token.
     * @return True if the token has changed
     */
    public synchronized boolean pushNotificationToken(@Nullable String notificationToken) {
        mCachedDevice = getCachedDevice();
        boolean changed = mCachedDevice.getNotificationsToken() == null ? notificationToken != null : !mCachedDevice.getNotificationsToken().equals(notificationToken);
        mCachedDevice.setNotificationsToken(notificationToken);
        return changed;
    }

    /**
     * Adds the tags to the device.
     *
     * @param tags The tags.
     */
    public synchronized void addTags(@Nullable List<HaloSegmentationTag> tags) {
        mCachedDevice = getCachedDevice();
        if (tags != null) {
            mCachedDevice.addTags(tags);
        }
    }

    /**
     * Remove the tags given the names.
     *
     * @param tagNames The tag names.
     */
    public synchronized void removeTags(@Nullable List<String> tagNames) {
        mCachedDevice = getCachedDevice();
        if (tagNames != null) {
            for (String tag : tagNames) {
                if (tag != null) {
                    mCachedDevice.removeTag(new HaloSegmentationTag(tag, null));
                }
            }
        }
    }

    /**
     * Provides always a device, even if it is an empty one.
     *
     * @return The device provided.
     */
    private Device getAlwaysDevice() {
        Device resultingDevice = null;
        try {
            //Bring cached
            String cached = mDeviceLocalDatasource.getCachedDevice();
            if (cached != null) {
                resultingDevice = Device.deserialize(cached, mParser);
            }
        } catch (HaloParsingException e) {
            //Just log the case
            Halog.e(getClass(), "The device stored was malformed. Overload it", e);
        }
        //Initialize if null for some reason
        if (resultingDevice == null) {
            resultingDevice = new Device();
        }
        return resultingDevice;
    }

    /**
     * Clears the cached device.
     */
    private void clearCachedDevice(){
        mCachedDevice = null;
        mDeviceLocalDatasource.clearCurrentDevice();
    }
}
