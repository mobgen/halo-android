package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;

/**
 * Created by f.souto.gonzalez on 16/06/2017.
 */

/**
 * HaloModule options to create a new request.
 *
 */
@Keep
public class HaloModuleQuery implements Parcelable {

    /**
     * Provides info to print module metadata.
     */
    private boolean mMetadaFields;

    /**
     * The server cache in seconds
     */
    private int mCacheServer;

    protected HaloModuleQuery() {

    }

    /**
     * private constructor to fromCursor search options.
     *
     * @param builder The builder used to fromCursor the original object.
     */
    protected HaloModuleQuery(@NonNull Builder builder) {
        mCacheServer = builder.mCacheServer;
        mMetadaFields = builder.mMetadaFields;
    }

    protected HaloModuleQuery(Parcel in) {
        mMetadaFields = in.readByte() != 0;
        mCacheServer = in.readInt();
    }

    public static final Creator<HaloModuleQuery> CREATOR = new Creator<HaloModuleQuery>() {
        @Override
        public HaloModuleQuery createFromParcel(Parcel in) {
            return new HaloModuleQuery(in);
        }

        @Override
        public HaloModuleQuery[] newArray(int size) {
            return new HaloModuleQuery[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mMetadaFields ? 1 : 0));
        dest.writeInt(mCacheServer);
    }

    @Keep
    @Api(2.33)
    public boolean withFields() {
        return mMetadaFields;
    }

    @Keep
    @Api(2.33)
    public int serverCahe() {
        return mCacheServer;
    }

    /**
     * Creates a new builder.
     *
     * @return The builder created.
     */
    @Keep
    @Api(2.33)
    @NonNull
    public static Builder builder() {
        return new Builder();
    }


    /**
     * The builder for the options item.
     */
    @Keep
    public static class Builder implements IBuilder<HaloModuleQuery> {
        /**
         * Provides info to print module metadata.
         */
        private boolean mMetadaFields;

        /**
         * The server cache in seconds
         */
        private int mCacheServer;


        /**
         * Constructor to get a new builder.
         */
        protected Builder() {
            mMetadaFields = false;
            mCacheServer = 0;
        }

        /**
         * Provides info to print modules metadata.
         *
         * @param printMetadata True if you want to print module metadata; otherwise false.
         * @return The current builder.
         */
        @Keep
        @Api(2.33)
        @NonNull
        public Builder withFields(boolean printMetadata) {
            mMetadaFields = printMetadata;
            return this;
        }


        /**
         * Set server cache in seconds when cache will expire.
         *
         * @param timeInSeconds The time when cache will expire in seconds
         * @return The current builder.
         */
        @Keep
        @Api(2.33)
        @NonNull
        public Builder serverCache(int timeInSeconds) {
            mCacheServer = timeInSeconds;
            return this;
        }

        @Keep
        @Api(2.33)
        @NonNull
        @Override
        public HaloModuleQuery build() {
            return new HaloModuleQuery(this);
        }
    }
}
