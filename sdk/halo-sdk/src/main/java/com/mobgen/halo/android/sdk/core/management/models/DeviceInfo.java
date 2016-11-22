package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.sdk.BuildConfig;

/**
 * The device on which the segmentation will be taken place.
 */
@Keep
@JsonObject
public class DeviceInfo implements Parcelable {

    /**
     * The notification token for this device.
     */
    @JsonField(name = "token")
    String mNotificationToken;

    /**
     * The platform name. This value is always hardcoded depending on the SDK.
     */
    @JsonField(name = "platform")
    String mPlatform;

    /**
     * The creator.
     */
    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Parcelable.Creator<DeviceInfo>() {
        public DeviceInfo createFromParcel(Parcel source) {
            return new DeviceInfo(source);
        }

        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };

    /**
     * Parsing empty constructor.
     */
    protected DeviceInfo() {
        //Empty constructor for parsing
    }

    /**
     * Constructor for the device.
     *
     * @param token The notification token that will be used.
     */
    @Api(1.0)
    public DeviceInfo(String token) {
        mNotificationToken = token;
        mPlatform = BuildConfig.HALO_PLATFORM_NAME;
    }

    /**
     * Constructor for the parcel interface.
     *
     * @param in The parcel.
     */
    protected DeviceInfo(Parcel in) {
        this.mNotificationToken = in.readString();
        this.mPlatform = in.readString();
    }

    /**
     * Provides the notification token identifier.
     *
     * @return The notification token identifier.
     */
    @Api(1.0)
    public String getNotificationToken() {
        return mNotificationToken;
    }

    /**
     * Provides the platform name.
     *
     * @return The platform name.
     */
    @Api(1.0)
    public String getPlatform() {
        return mPlatform;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mNotificationToken);
        dest.writeString(this.mPlatform);
    }
}
