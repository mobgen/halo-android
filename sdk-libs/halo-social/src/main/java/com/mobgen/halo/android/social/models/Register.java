package com.mobgen.halo.android.social.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.sdk.core.management.models.Device;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Identified user with some network
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
        mHaloAuthProfile = builder.mHaloAuthProfile;
        mHaloUserProfile = builder.mHaloUserProfile;
    }

    /**
     * Builder class for register.
     */
    protected Register(Parcel in) {
        mHaloAuthProfile = in.readParcelable(HaloAuthProfile.class.getClassLoader());
        mHaloUserProfile = in.readParcelable(HaloUserProfile.class.getClassLoader());
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
            AssertionUtils.notNull(haloAuthProfile, "haloAuthProfile");
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

    /**
     * Provides the serializer given the factory.
     *
     * @param register The object to serialize.
     * @param parser   The parser factory.
     * @return The parser obtained.
     */
    public static String serialize(@NonNull Register register, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(register, "register");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<Register, String>) parser.serialize(Register.class)).convert(register);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the device", e);
        }
    }

    /**
     * Parses the device stored in the preferences.
     *
     * @param register   The register as string.
     * @param parser The parser.
     * @return The register parsed or an empty register if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static Register deserialize(@Nullable String register, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (register != null) {
            try {
                return ((Parser<InputStream, Register>) parser.deserialize(Register.class)).convert(new ByteArrayInputStream(register.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the register", e);
            }
        }
        return null;
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
