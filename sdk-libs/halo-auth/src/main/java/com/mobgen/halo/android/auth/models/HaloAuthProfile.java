package com.mobgen.halo.android.auth.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;


/**
 * Authorization profile
 */
@Keep
@JsonObject
public class HaloAuthProfile implements Parcelable {

    /**
     * The email.
     */
    @Nullable
    @JsonField(name = "email")
    String mEmail;
    /**
     * The password.
     */
    @Nullable
    @JsonField(name = "password")
    String mPassword;
    /**
     * The device alias.
     */
    @Nullable
    @JsonField(name = "deviceId")
    String mAlias;

    /**
     * Parsing empty constructor.
     */
    protected HaloAuthProfile() {
        //Empty constructor for parsing
    }

    public static final Creator<HaloAuthProfile> CREATOR = new Creator<HaloAuthProfile>() {
        @Override
        public HaloAuthProfile createFromParcel(Parcel source) {
            return new HaloAuthProfile(source);
        }

        @Override
        public HaloAuthProfile[] newArray(int size) {
            return new HaloAuthProfile[size];
        }
    };

    /**
     * The auth profile
     */
    public HaloAuthProfile(String email, String password) {
        this.mEmail = email;
        this.mPassword = password;
    }

    /**
     * The auth profile
     */
    public HaloAuthProfile(String email, String password, String alias) {
        this.mEmail = email;
        this.mPassword = password;
        this.mAlias = alias;
    }

    /**
     * Provides the email
     *
     * @return The the email.
     */
    @Api(2.1)
    @Nullable
    public String getEmail() {
        return mEmail;
    }

    /**
     * Provides the password
     *
     * @return The the password.
     */
    @Api(2.1)
    @Nullable
    public String getPassword() {
        return mPassword;
    }

    /**
     * Provides the alias of the device
     *
     * @return The the alias.
     */
    @Api(2.1)
    @Nullable
    public String getAlias() {
        return mAlias;
    }

    /**
     * Set the alias of the device
     *
     * @return The the alias.
     */
    @Api(2.1)
    @Nullable
    public String setAlias(String alias) {
        return mAlias = alias;
    }


    /**
     * Parcel for the authProfile.
     *
     * @param in The parcel where we will write the auth profile
     */
    protected HaloAuthProfile(Parcel in) {
        this.mEmail = in.readString();
        this.mPassword = in.readString();
        this.mAlias = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mEmail);
        dest.writeString(this.mPassword);
        dest.writeString(this.mAlias);
    }

    @Override
    public String toString() {
        return "HaloAuthProfile{" +
                "  mEmail='" + mEmail + '\'' +
                ", mPassword=" + mPassword +
                ", mAlias='" + mAlias + '\'' +
                '}';
    }
}
