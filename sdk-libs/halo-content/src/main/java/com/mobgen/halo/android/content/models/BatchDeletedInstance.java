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
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by f.souto.gonzalez on 11/05/2017.
 */

/**
 * Represent the result of a truncate operation. Contains the number of elements deleted.
 */
@JsonObject
@Keep
public class BatchDeletedInstance implements Parcelable {

    @JsonField(name = "deletedInstances")
    int mDeletedInstaces;

    protected BatchDeletedInstance() {

    }

    /**
     * The numer of deleted instanes after a truncate operation
     *
     * @return The number of deleted instances
     */
    @Api(2.3)
    public int getDeletedInstaces() {
        return mDeletedInstaces;
    }

    protected BatchDeletedInstance(Parcel in) {
        mDeletedInstaces = in.readInt();
    }

    public static final Creator<BatchDeletedInstance> CREATOR = new Creator<BatchDeletedInstance>() {
        @Override
        public BatchDeletedInstance createFromParcel(Parcel in) {
            return new BatchDeletedInstance(in);
        }

        @Override
        public BatchDeletedInstance[] newArray(int size) {
            return new BatchDeletedInstance[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDeletedInstaces);
    }

    /**
     * Parses a Halo content instance.
     *
     * @param batchDeletions The batchDeletions as string.
     * @param parser         The parser.
     * @return The batchDeletions parsed or an empty batchDeletions if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static BatchDeletedInstance deserialize(@Nullable String batchDeletions, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (batchDeletions != null) {
            try {
                return ((Parser<InputStream, BatchDeletedInstance>) parser.deserialize(BatchDeletedInstance.class)).convert(new ByteArrayInputStream(batchDeletions.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the BatchDeletedInstance", e);
            }
        }
        return null;
    }

}
