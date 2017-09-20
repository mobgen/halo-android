package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Device data object used to store the information of the current device.
 */
@Keep
@JsonObject
public class Device implements Parcelable {

    /**
     * The alias used by the system to identify the device.
     */
    @JsonField(name = "alias")
    String mAlias;

    /**
     * The id of the device data.
     */
    @JsonField(name = "id")
    String mId;

    /**
     * The email of the device.
     */
    @JsonField(name = "email")
    String mEmail;

    /**
     * The devices for the current device.
     */
    @JsonField(name = "devices")
    List<DeviceInfo> mDeviceInfo;

    /**
     * The segmentation tags linked to the device.
     */
    @JsonField(name = "tags")
    List<HaloSegmentationTag> mTags;

    /**
     * Flag to replace the tokens.
     */
    @JsonField(name = "replaceTokens")
    boolean mReplaceToken = true;

    /**
     * the application id
     */
    @JsonField(name = "appId")
    String mAppId;
    /**
     * The creator.
     */
    public static final Creator<Device> CREATOR = new Creator<Device>() {
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    /**
     * Constructor for the device.
     */
    public Device() {
        mTags = new ArrayList<>();
    }

    /**
     * Constructor for the device.
     *
     * @param alias             The alias.
     * @param id                The id.
     * @param email             The email for the device.
     * @param notificationToken The notification token.
     */
    public Device(@Nullable String alias, @Nullable String id, @Nullable String email, @Nullable String notificationToken, @Nullable String appId) {
        this();
        mAlias = alias;
        mId = id;
        mEmail = email;
        mAppId = appId;
        setNotificationsToken(notificationToken);
    }

    /**
     * Constructor for the parcelable interface.
     *
     * @param in The parcel.
     */
    protected Device(Parcel in) {
        this.mAlias = in.readString();
        this.mId = in.readString();
        this.mEmail = in.readString();
        this.mAppId = in.readString();
        this.mDeviceInfo = in.createTypedArrayList(DeviceInfo.CREATOR);
        this.mTags = in.createTypedArrayList(HaloSegmentationTag.CREATOR);
    }

    /**
     * Provides the app id.
     *
     * @return The application id.
     */
    @Api(2.3)
    @Nullable
    public String getAppId() {
        return mAppId;
    }

    /**
     * Provides the alias of a device.
     *
     * @return The alias of the device.
     */
    @Api(1.0)
    @Nullable
    public String getAlias() {
        return mAlias;
    }

    /**
     * Provides the email of the device synchronized with this account.
     *
     * @return The email.
     */
    @Api(1.0)
    @Nullable
    public String getEmail() {
        return mEmail;
    }

    /**
     * Provides the device tags registered.
     *
     * @return The device tag registered.
     */
    @Api(1.0)
    @Nullable
    public List<DeviceInfo> getDevices() {
        return mDeviceInfo;
    }

    /**
     * Sets the notification token on the devices list.
     *
     * @param notificationToken The notification token to set.
     */
    @Api(2.0)
    public void setNotificationsToken(@Nullable String notificationToken) {
        if (notificationToken != null) {
            if (mDeviceInfo == null) {
                mDeviceInfo = new ArrayList<>(1);
            }
            mDeviceInfo.clear();
            mDeviceInfo.add(new DeviceInfo(notificationToken));
        }
    }

    /**
     * Provides the notification token.
     *
     * @return The notification token or null if it is not present.
     */
    @Nullable
    @Api(1.0)
    public String getNotificationsToken() {
        if (mDeviceInfo != null && !mDeviceInfo.isEmpty()) {
            return mDeviceInfo.get(0).getNotificationToken();
        }
        return null;
    }

    /**
     * Provides the request id.
     *
     * @return The request id.
     */
    @Api(1.0)
    @Nullable
    public String getId() {
        return mId;
    }

    /**
     * Provides the tags as a copy of the original ones. Use {@link #addTag(HaloSegmentationTag) addTag} or
     * {@link #removeTag(HaloSegmentationTag) removeTag} methods to manage the tags.
     *
     * @return The tags.
     */
    @Api(1.0)
    @NonNull
    public List<HaloSegmentationTag> getTags() {
        return new ArrayList<>(mTags);
    }

    /**
     * Replaces the system tags with the ones provided as a parameter.
     *
     * @param systemTags The system tags.
     */
    @Api(1.0)
    public void addTags(@NonNull List<HaloSegmentationTag> systemTags, boolean shouldOverrideTags) {
        for (HaloSegmentationTag tag : systemTags) {
            if (tag != null) {
                if (shouldOverrideTags) {
                    addTag(tag);
                } else {
                    addRepeatedKeyTags(tag);
                }
            }
        }
    }

    /**
     * Adds a distinct tag to the device. If the tag exist with the same key it will be override.
     *
     * @param segmentationTag The tag to add to the device.
     */
    @Api(1.0)
    public void addTag(@NonNull HaloSegmentationTag segmentationTag) {
        if (mTags.contains(segmentationTag)) {
            int tagSize = mTags.size();
            for (int i = 0; i < tagSize; i++) {
                if (mTags.contains(segmentationTag)) {
                    mTags.remove(segmentationTag);
                }
            }
            mTags.add(segmentationTag);
        } else {
            mTags.add(segmentationTag);
        }
    }

    /**
     * Adds a new tag to the device. If the tag has the same key it will create a new item with that key and value.
     *
     * @param segmentationTags The tag to add to the device.
     */
    @Api(2.33)
    public void addRepeatedKeyTags(@NonNull HaloSegmentationTag... segmentationTags) {
        AssertionUtils.notNull(segmentationTags, "segmentationTag");
        addToList(mTags, segmentationTags);
    }

    /**
     * Adds something to the given list or creates it returning as a result.
     *
     * @param list  The list of items.
     * @param items The items.
     * @return The list returned or created.
     */
    @NonNull
    public static <T> List<T> addToList(@Nullable List<T> list, @Nullable T[] items) {
        List<T> finalList = list;
        if (items != null && items.length > 0) {
            if (finalList == null) {
                finalList = new ArrayList<>();
            }
            finalList.addAll(Arrays.asList(items));
        }
        return finalList;
    }

    /**
     * Removes a segmentation tag.
     *
     * @param segmentationTag The segmentation tag.
     * @return True if it was removed. False otherwise.
     */
    @Api(1.0)
    public boolean removeTag(HaloSegmentationTag segmentationTag) {
        return mTags.remove(segmentationTag);
    }

    /**
     * Provides the serializer given the factory.
     *
     * @param parser The parser factory.
     * @return The parser obtained.
     */
    @Api(2.0)
    @SuppressWarnings("unchecked")
    public static String serialize(@NonNull Device device, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(device, "device");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<Device, String>) parser.serialize(Device.class)).convert(device);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the device", e);
        }
    }

    /**
     * Parses the device stored in the preferences.
     *
     * @param device The device as string.
     * @param parser The parser.
     * @return The device parsed or an empty device if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static Device deserialize(@Nullable String device, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (device != null) {
            try {
                return ((Parser<InputStream, Device>) parser.deserialize(Device.class)).convert(new ByteArrayInputStream(device.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the device", e);
            }
        }
        return null;
    }

    /**
     * Makes this device as anonymous so in the next update it will create a new one instead of
     * updating the previous one.
     */
    public void makeAnonymous() {
        mId = null;
        mAlias = null;
    }

    /**
     * The device has not been synchronized yet.
     *
     * @return True if it anonymous, false otherwise.
     */
    public boolean isAnonymous() {
        return mId == null && mAlias == null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAlias);
        dest.writeString(this.mId);
        dest.writeString(this.mEmail);
        dest.writeString(this.mAppId);
        dest.writeTypedList(mDeviceInfo);
        dest.writeTypedList(mTags);
    }
}
