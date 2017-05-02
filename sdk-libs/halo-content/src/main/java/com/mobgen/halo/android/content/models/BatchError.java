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
 * Created by fernandosouto on 19/04/17.
 */
@Keep
@JsonObject
public class BatchError implements Parcelable {

    @JsonField(name = "error")
    BatchErrorInfo mError;

    protected BatchError() {

    }

    public static final Creator<BatchError> CREATOR = new Creator<BatchError>() {
        @Override
        public BatchError createFromParcel(Parcel in) {
            return new BatchError(in);
        }

        @Override
        public BatchError[] newArray(int size) {
            return new BatchError[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected BatchError(Parcel in) {
        this.mError = in.readParcelable(BatchErrorInfo.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mError, flags);
    }

    /**
     * Parses a batch error.
     *
     * @param batchError The batcherror as string.
     * @param parser     The parser.
     * @return The batchError parsed or an empty batchError if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static BatchError deserialize(@Nullable String batchError, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (batchError != null) {
            try {
                return ((Parser<InputStream, BatchError>) parser.deserialize(BatchError.class)).convert(new ByteArrayInputStream(batchError.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the batchError", e);
            }
        }
        return null;
    }

    /**
     * Get the error info.
     *
     * @return The error info.
     */
    @NonNull
    @Api(2.3)
    public BatchErrorInfo getError() {
        return mError;
    }
}
