package com.mobgen.halo.android.social.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * Identified user with some network
 *
 */
@Keep
@JsonObject
public class Register implements Parcelable {

    /**
     * The authorization profile.
     */
    @NonNull
    @JsonField(name = "auth")
    HaloAuthProfile mHaloAuthProfile;
    /**
     * The user profile.
     */
    @Nullable
    @JsonField(name = "profile")
    HaloUserProfile mHaloUserProfile;

    /**
     * Constructor of the Register. This constructor should be only used by Gson while parsing.
     */
    public Register() {

    }

    public static final Creator<Register> CREATOR = new Creator<Register>() {
        @Override
        public Register createFromParcel(Parcel source) {
            return new Register(source);
        }

        @Override
        public Register[] newArray(int size) {
            return new Register[size];
        }
    };

    /**
     * A register.
     *
     * @param builder The builder.
     */
    private Register(@NonNull Builder builder) {
        mHaloUserProfile = builder.mHaloUserProfile;
        mHaloAuthProfile = builder.mHaloAuthProfile;
    }

    /**
     * Builder class for register.
     */
    protected Register(Parcel in) {
        mHaloUserProfile = in.readParcelable(HaloUserProfile.class.getClassLoader());
        mHaloAuthProfile = in.readParcelable(HaloAuthProfile.class.getClassLoader());
    }

    /**
     * Creates the builder given the haloAuthProfile and haloUserProfile.
     *
     * @param haloAuthProfile The auth profile.
     * @param haloUserProfile The user profile.
     * @return The builder created.
     */
    @Api(2.0)
    public Register(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) {
        mHaloAuthProfile = haloAuthProfile;
        mHaloUserProfile = haloUserProfile;
    }

    /**
     * Provides the user profile
     *
     * @return The user profile.
     */
    @Api(2.0)
    @NonNull
    public HaloUserProfile getUserProfile() {
        return mHaloUserProfile;
    }

    /**
     * Provides the auth profile
     *
     * @return The auth profile.
     */
    @Api(2.0)
    @Nullable
    public HaloAuthProfile getHaloAuthProfile() {
        return mHaloAuthProfile;
    }

    /**
     * Creates the builder given the auth profile and the user profile.
     *
     * @param haloAuthProfile The auth profile.
     * @param haloUserProfile The user profile.
     * @return The builder created.
     */
    @Api(2.0)
    @NonNull
    public static Builder builder(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) {
        return new Builder(haloAuthProfile, haloUserProfile);
    }

    /**
     * The builder class.
     */
    public static class Builder implements IBuilder<Register> {

        /**
         * The auth profile.
         */
        @NonNull
        private HaloAuthProfile mHaloAuthProfile;
        /**
         * The user profile.
         */
        @NonNull
        private HaloUserProfile mHaloUserProfile;

        /**
         * Creates the builder with auth profile and user profile.
         *
         * @param haloAuthProfile The auth profile.
         * @param haloUserProfile The user profile.
         */
        private Builder(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) {
            AssertionUtils.notNull(haloAuthProfile, "token");
            AssertionUtils.notNull(haloUserProfile, "haloUserProfile");
            mHaloAuthProfile = haloAuthProfile;
            mHaloUserProfile = haloUserProfile;
        }

        @NonNull
        @Override
        public Register build() {
            return new Register(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mHaloAuthProfile, flags);
        dest.writeParcelable(this.mHaloUserProfile, flags);
    }
}
