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
public class IdentifiedUser implements Parcelable {

    /**
     * The user
     */
    @NonNull
    @JsonField(name = "user")
    HaloUserProfile mHaloUserProfile;
    /**
     * The token
     */
    @Nullable
    @JsonField(name = "token")
    Token mToken;

    /**
     * Constructor of the IdentifiedUser. This constructor should be only used by Gson while parsing.
     */
    public IdentifiedUser() {

    }

    public static final Creator<IdentifiedUser> CREATOR = new Creator<IdentifiedUser>() {
        @Override
        public IdentifiedUser createFromParcel(Parcel source) {
            return new IdentifiedUser(source);
        }

        @Override
        public IdentifiedUser[] newArray(int size) {
            return new IdentifiedUser[size];
        }
    };

    /**
     * The identified user.
     *
     * @param builder The builder.
     */
    private IdentifiedUser(@NonNull Builder builder) {
        mHaloUserProfile = builder.mHaloUserProfile;
        mToken = builder.mToken;
    }

    /**
     * Builder class for the identified user.
     */
    protected IdentifiedUser(Parcel in) {
        mToken = in.readParcelable(Token.class.getClassLoader());
        mHaloUserProfile = in.readParcelable(HaloUserProfile.class.getClassLoader());
    }

    /**
     * Creates the builder given the token and the user id.
     *
     * @param token The token.
     * @param haloUserProfile The user.
     * @return The builder created.
     */
    @Api(2.0)
    public IdentifiedUser(@NonNull Token token,@NonNull HaloUserProfile haloUserProfile) {
        mToken=token;
        mHaloUserProfile = haloUserProfile;
    }

    /**
     * Provides the user profile
     *
     * @return The user profile.
     */
    @Api(2.0)
    @NonNull
    public HaloUserProfile getUser() {
        return mHaloUserProfile;
    }

    /**
     * Provides the token
     *
     * @return The token.
     */
    @Api(2.0)
    @Nullable
    public Token getToken() {
        return mToken;
    }

    /**
     * Creates the builder given the token and the user id.
     *
     * @param token The token.
     * @param haloUserProfile The user.
     * @return The builder created.
     */
    @Api(2.0)
    @NonNull
    public static Builder builder(@NonNull Token token,@NonNull HaloUserProfile haloUserProfile) {
        return new Builder(token, haloUserProfile);
    }

    /**
     * The builder class.
     */
    public static class Builder implements IBuilder<IdentifiedUser> {

        /**
         * The token id.
         */
        @NonNull
        private Token mToken;
        /**
         * The user id.
         */
        @NonNull
        private HaloUserProfile mHaloUserProfile;

        /**
         * Creates the builder with token and user id.
         *
         * @param token The token.
         * @param haloUserProfile The user.
         */
        private Builder(@NonNull Token token,@NonNull HaloUserProfile haloUserProfile) {
            AssertionUtils.notNull(token, "token");
            AssertionUtils.notNull(haloUserProfile, "haloUserProfile");
            mToken = token;
            mHaloUserProfile = haloUserProfile;
        }

        @NonNull
        @Override
        public IdentifiedUser build() {
            return new IdentifiedUser(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mToken, flags);
        dest.writeParcelable(this.mHaloUserProfile, flags);
    }
}
