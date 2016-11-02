package com.mobgen.halo.android.social;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * Common interface for the results that can be provided by different
 * social networks.
 */
public class HaloSocialProfile implements Parcelable {

    /**
     * The token id.
     */
    @NonNull
    private final String mSocialToken;
    /**
     * The user id.
     */
    @Nullable
    private final String mSocialId;
    /**
     * The email.
     */
    @Nullable
    private final String mEmail;
    /**
     * The photo.
     */
    @Nullable
    private final Uri mPhoto;
    /**
     * Display name with the name and surname all together.
     */
    @Nullable
    private final String mDisplayName;
    /**
     * The name.
     */
    @Nullable
    private final String mName;
    /**
     * The surname.
     */
    @Nullable
    private final String mSurname;

    public static final Creator<HaloSocialProfile> CREATOR = new Creator<HaloSocialProfile>() {
        @Override
        public HaloSocialProfile createFromParcel(Parcel source) {
            return new HaloSocialProfile(source);
        }

        @Override
        public HaloSocialProfile[] newArray(int size) {
            return new HaloSocialProfile[size];
        }
    };

    /**
     * THe social profile.
     *
     * @param builder The builder.
     */
    private HaloSocialProfile(@NonNull Builder builder) {
        mSocialToken = builder.mSocialToken;
        mSocialId = builder.mSocialId;
        mDisplayName = builder.mDisplayName;
        mName = builder.mName;
        mSurname = builder.mSurname;
        mPhoto = builder.mPhoto;
        mEmail = builder.mEmail;
    }

    /**
     * Builder class for the social profile.
     */
    protected HaloSocialProfile(Parcel in) {
        this.mSocialToken = in.readString();
        this.mSocialId = in.readString();
        this.mEmail = in.readString();
        this.mPhoto = in.readParcelable(Uri.class.getClassLoader());
        this.mDisplayName = in.readString();
        this.mName = in.readString();
        this.mSurname = in.readString();
    }

    /**
     * The token id. This token is the unique identifier per app and per
     * user.
     *
     * @return The token id.
     */
    @Api(2.0)
    @NonNull
    public String socialToken() {
        return mSocialToken;
    }

    /**
     * Provides the user id.
     *
     * @return The user id.
     */
    @Api(2.0)
    @Nullable
    public String socialId() {
        return mSocialId;
    }

    /**
     * The email of the user.
     *
     * @return The email.
     */
    @Api(2.0)
    @Nullable
    public String email() {
        return mEmail;
    }

    /**
     * The photo of the user.
     *
     * @return The photo.
     */
    @Api(2.0)
    @Nullable
    public Uri photo() {
        return mPhoto;
    }

    /**
     * The display name of the user.
     *
     * @return The display name.
     */
    @Api(2.0)
    @Nullable
    public String displayName() {
        return mDisplayName;
    }

    /**
     * The name of the user.
     *
     * @return The name.
     */
    @Api(2.0)
    @Nullable
    public String name() {
        return mName;
    }

    /**
     * The surname of the user.
     *
     * @return The surname.
     */
    @Api(2.0)
    @Nullable
    public String surname() {
        return mSurname;
    }

    /**
     * Creates the builder given the token and the user id.
     *
     * @param socialToken The token.
     * @return The builder created.
     */
    @Api(2.0)
    @NonNull
    public static Builder builder(@NonNull String socialToken) {
        return new Builder(socialToken);
    }

    /**
     * The builder class.
     */
    public static class Builder implements IBuilder<HaloSocialProfile> {
        /**
         * The token id.
         */
        @NonNull
        private final String mSocialToken;
        /**
         * The user id.
         */
        private String mSocialId;
        /**
         * The email.
         */
        @Nullable
        private String mEmail;
        /**
         * The photo.
         */
        @Nullable
        private Uri mPhoto;
        /**
         * Display name with the name and surname all together.
         */
        @Nullable
        private String mDisplayName;
        /**
         * The name.
         */
        @Nullable
        private String mName;

        /**
         * The surname.
         */
        @Nullable
        private String mSurname;

        /**
         * Creates the builder with token and user id.
         *
         * @param socialToken The token id.
         */
        private Builder(@NonNull String socialToken) {
            AssertionUtils.notNull(socialToken, "socialToken");
            mSocialToken = socialToken;
        }

        /**
         * The social id.
         *
         * @param socialId The social id.
         * @return The current builder.
         */
        @NonNull
        public Builder socialId(@Nullable String socialId) {
            mSocialId = socialId;
            return this;
        }

        /**
         * Sets the email.
         *
         * @param email The email.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder email(@Nullable String email) {
            mEmail = email;
            return this;
        }

        /**
         * Sets the photo.
         *
         * @param photo The photo.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder photo(@Nullable Uri photo) {
            mPhoto = photo;
            return this;
        }

        /**
         * Sets the displayName.
         *
         * @param displayName The displayName.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder displayName(@Nullable String displayName) {
            mDisplayName = displayName;
            return this;
        }

        /**
         * Sets the name.
         *
         * @param name The name.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder name(@Nullable String name) {
            mName = name;
            return this;
        }

        /**
         * Sets the surname.
         *
         * @param surname The surname.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder surname(@Nullable String surname) {
            mSurname = surname;
            return this;
        }

        @NonNull
        @Override
        public HaloSocialProfile build() {
            return new HaloSocialProfile(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSocialToken);
        dest.writeString(this.mSocialId);
        dest.writeString(this.mEmail);
        dest.writeParcelable(this.mPhoto, flags);
        dest.writeString(this.mDisplayName);
        dest.writeString(this.mName);
        dest.writeString(this.mSurname);
    }

    @Override
    public String toString() {
        return "HaloSocialProfile{" +
                "mSocialToken='" + mSocialToken + '\'' +
                ", mSocialId='" + mSocialId + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mPhoto=" + mPhoto +
                ", mDisplayName='" + mDisplayName + '\'' +
                ", mName='" + mName + '\'' +
                ", mSurname='" + mSurname + '\'' +
                '}';
    }
}
