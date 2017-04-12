package com.mobgen.halo.android.content.models;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fernandosouto on 05/04/17.
 */

/**
 * Batch operations to perfom. It contains all operations to perfom.
 */
@JsonObject
@Keep
public class BatchOperations implements Parcelable {

    @JsonField(name = "truncate")
    List<HaloContentInstance> mTruncate;
    @JsonField(name = "created")
    List<HaloContentInstance> mCreated;
    @JsonField(name = "updated")
    List<HaloContentInstance> mUpdated;
    @JsonField(name = "createdOrUpdated")
    List<HaloContentInstance> mCreatedOrUpdated;
    @JsonField(name = "deleted")
    List<HaloContentInstance> mDeleted;

    /**
     * Constructor for the advanced batch operation
     */
    public BatchOperations() {
        //Constructor for the class.
    }

    public static final Creator<BatchOperations> CREATOR = new Creator<BatchOperations>() {
        @Override
        public BatchOperations createFromParcel(Parcel in) {
            return new BatchOperations(in);
        }

        @Override
        public BatchOperations[] newArray(int size) {
            return new BatchOperations[size];
        }
    };

    protected BatchOperations(Parcel in) {
        this.mTruncate = in.createTypedArrayList(HaloContentInstance.CREATOR);
        this.mCreated = in.createTypedArrayList(HaloContentInstance.CREATOR);
        this.mUpdated = in.createTypedArrayList(HaloContentInstance.CREATOR);
        this.mCreatedOrUpdated = in.createTypedArrayList(HaloContentInstance.CREATOR);
        this.mDeleted = in.createTypedArrayList(HaloContentInstance.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mTruncate);
        dest.writeTypedList(mCreated);
        dest.writeTypedList(mUpdated);
        dest.writeTypedList(mCreatedOrUpdated);
        dest.writeTypedList(mDeleted);
    }

    /**
     * Serializes a instance.
     *
     * @param haloContentInstance The object to serialize.
     * @param parser              The parser.
     * @return The instace seriliazed.
     * @throws HaloParsingException
     */
    public static String serialize(@NonNull BatchOperations haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<BatchOperations, String>) parser.serialize(BatchOperations.class)).convert(haloContentInstance);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the HaloContentInstance", e);
        }
    }

    private BatchOperations(@NonNull Builder builder) {
        mTruncate = builder.mTruncate;
        mCreated = builder.mCreated;
        mUpdated = builder.mUpdated;
        mCreatedOrUpdated = builder.mCreatedOrUpdated;
        mDeleted = builder.mDeleted;
    }

    /**
     * Get the truncate operations.
     *
     * @return A list with all truncate operations
     */
    @NonNull
    @Api(2.3)
    public List<HaloContentInstance> getTruncate() {
        return mTruncate;
    }

    /**
     * Get the create operations.
     *
     * @return A list with all create operations.
     */
    @NonNull
    @Api(2.3)
    public List<HaloContentInstance> getCreated() {
        return mCreated;
    }

    /**
     * Get the update operations.
     *
     * @return A list with all the update operations.
     */
    @NonNull
    @Api(2.3)
    public List<HaloContentInstance> getUpdated() {
        return mUpdated;
    }

    /**
     * Get the createorupdate operations.
     *
     * @return A list with all createorupdate operations.
     */
    @NonNull
    @Api(2.3)
    public List<HaloContentInstance> getCreatedOrUpdated() {
        return mCreatedOrUpdated;
    }

    /**
     * Get the delete operations.
     *
     * @return A list with all delete operations.
     */
    @NonNull
    @Api(2.3)
    public List<HaloContentInstance> getDeleted() {
        return mDeleted;
    }

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<BatchOperations> {

        /**
         * The truncate operations.
         */
        List<HaloContentInstance> mTruncate;

        /**
         * The create operations.
         */
        List<HaloContentInstance> mCreated;

        /**
         * The update operations.
         */
        List<HaloContentInstance> mUpdated;

        /**
         * The createorupdate operations.
         */
        List<HaloContentInstance> mCreatedOrUpdated;

        /**
         * The delete operations.
         */
        List<HaloContentInstance> mDeleted;

        /**
         * Add truncate operations.
         *
         * @param truncate The truncate operations.
         * @return The builder
         */
        @NonNull
        @Api(2.3)
        public BatchOperations.Builder truncate(@NonNull HaloContentInstance... truncate) {
            if (mTruncate == null) {
                mTruncate = new ArrayList<>();
            }
            mTruncate = addToList(mTruncate, truncate);
            return this;
        }

        /**
         * Add create operations.
         *
         * @param create The create operations.
         * @return The builder
         */
        @NonNull
        @Api(2.3)
        public BatchOperations.Builder create(@NonNull HaloContentInstance... create) {
            if (mCreated == null) {
                mCreated = new ArrayList<>();
            }
            mCreated = addToList(mCreated, create);
            return this;
        }

        /**
         * Add update operations.
         *
         * @param update The update operations.
         * @return The builder
         */
        @NonNull
        @Api(2.3)
        public BatchOperations.Builder update(@NonNull HaloContentInstance... update) {
            if (mUpdated == null) {
                mUpdated = new ArrayList<>();
            }
            mUpdated = addToList(mUpdated, update);
            return this;
        }

        /**
         * Add create or update operations.
         *
         * @param createOrUpdate The create or update operations.
         * @return The builder
         */
        @NonNull
        @Api(2.3)
        public BatchOperations.Builder createOrUpdate(@NonNull HaloContentInstance... createOrUpdate) {
            if (mCreatedOrUpdated == null) {
                mCreatedOrUpdated = new ArrayList<>();
            }
            mCreatedOrUpdated = addToList(mCreatedOrUpdated, createOrUpdate);
            return this;
        }

        /**
         * Add delete operations.
         *
         * @param delete The delete operations.
         * @return The builder
         */
        @NonNull
        @Api(2.3)
        public BatchOperations.Builder delete(@NonNull HaloContentInstance... delete) {
            if (mDeleted == null) {
                mDeleted = new ArrayList<>();
            }
            mDeleted = addToList(mDeleted, delete);
            return this;
        }

        @NonNull
        @Override
        public BatchOperations build() {
            return new BatchOperations(this);
        }
    }

    /**
     * Adds something to the given list or creates it returning as a result.
     *
     * @param list  The list of items.
     * @param items The items.
     * @return The list returned or created.
     */
    private static <T> List<T> addToList(@Nullable List<T> list, @Nullable T[] items) {
        List<T> finalList = list;
        if (items != null && items.length > 0) {
            if (finalList == null) {
                finalList = new ArrayList<>();
            }
            finalList.addAll(Arrays.asList(items));
        }
        return finalList;
    }

}
