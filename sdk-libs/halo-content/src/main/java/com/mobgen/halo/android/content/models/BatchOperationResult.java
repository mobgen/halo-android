package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandosouto on 05/04/17.
 */

/**
 * Result from remote data source for a batch operation.
 */
@JsonObject
@Keep
public class BatchOperationResult implements Parcelable {

    /**
     * Json converter to transform the json that comes to a json object.
     */
    @Keep
    public static class JSONObjectConverter implements TypeConverter<Object> {

        /**
         * The mapper for objects.
         */
        private static final JsonMapper<Object> mapper = LoganSquare.mapperFor(Object.class);

        @Override
        public Object parse(JsonParser jsonParser) throws IOException {
            //convert map to json string to create the object due to problems on pre 4.3 devices
            //See https://mobgen.atlassian.net/browse/HALO-2918
            Map map = null;
            Object mapResult = mapper.parse(jsonParser);
            try {
                map = (Map) mapResult;
                return new JSONObject(LoganSquare.serialize(map));
            } catch (Exception jsonException) {
                try {
                    List<HaloContentInstance> haloContentInstances = (List<HaloContentInstance>) mapResult;
                    return haloContentInstances != null ? new JSONArray(haloContentInstances) : null;
                } catch (Exception jsonArryaException){
                    return null;
                }
            }
        }

        @Override
        public void serialize(Object object, String fieldName,
                              boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
        }
    }

    @JsonField(name = "operation")
    String mOperation;
    @JsonField(name = "position")
    int mPosition;
    @JsonField(name = "success")
    boolean mSuccess;
    @JsonField(name = "data", typeConverter = BatchOperationResult.JSONObjectConverter.class)
    Object mData;

    protected BatchOperationResult() {

    }

    public BatchOperationResult(String operation, int position, boolean success, Object data){
        mPosition = position;
        mOperation = operation;
        mSuccess = success;
        mData = data;
    }

    public static final Creator<BatchOperationResult> CREATOR = new Creator<BatchOperationResult>() {
        @Override
        public BatchOperationResult createFromParcel(Parcel in) {
            return new BatchOperationResult(in);
        }

        @Override
        public BatchOperationResult[] newArray(int size) {
            return new BatchOperationResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected BatchOperationResult(Parcel in) {
        this.mOperation = in.readString();
        this.mPosition = in.readInt();
        mSuccess = in.readByte() != 0;
        this.mData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOperation);
        dest.writeInt(this.mPosition);
        dest.writeByte((byte) (mSuccess ? 1 : 0));
        dest.writeString(this.mData.toString());
    }

    /**
     * Get the position of the element.
     *
     * @return The position of the element on his array.
     */
    @Api(2.3)
    public int getPosition() {
        return mPosition;
    }

    /**
     * Get the status of current operation.
     *
     * @return True if operation was success; Otherwise false.
     */
    @Api(2.3)
    public boolean isSuccess() {
        return mSuccess;
    }

    /**
     * Get the data of the operation.
     *
     * @return The data to parse.
     */
    @Api(2.3)
    @NonNull
    public String getRawData() {
        return mData.toString();
    }

    /**
     * Get the data of the operation. parsed
     *
     * @return The data parsed as HaloContentInstance or BatchError
     */
    @Api(2.3)
    @Nullable
    public HaloContentInstance getData() {
        try {
            if (mSuccess && mOperation != BatchOperator.TRUNCATE) {
                return HaloContentInstance.deserialize(mData.toString(), Halo.instance().framework().parser());
            }
        } catch (HaloParsingException parsingExcetion) {
            return null;
        }
        return null;
    }

    /**
     * Get the data of the operation truncate
     *
     * @return The data parsed as List<HaloContentInstance>
     */
    @Api(2.3)
    @Nullable
    public List<HaloContentInstance> getDataTruncate() {
        try {
            if (mSuccess && mOperation == BatchOperator.TRUNCATE) {
                JsonMapper<HaloContentInstance> mapper =  LoganSquare.mapperFor(HaloContentInstance.class);
                return mapper.parseList(mData.toString());
            }
        } catch (IOException e) {
            return null;
        }
        return null;

    }

    /**
     * Get the data of the operation. parsed
     *
     * @return The data parsed as HaloContentInstance or BatchError
     */
    @Api(2.3)
    @Nullable
    public BatchError getDataError() {
        try {
            if (!mSuccess && mOperation != BatchOperator.TRUNCATE) {
                return BatchError.deserialize(mData.toString(), Halo.instance().framework().parser());
            }
        } catch (HaloParsingException parsingExcetion) {
            return null;
        }
        return null;
    }

    /**
     * Get the operation that was performed.
     *
     * @return The operation
     */
    @NonNull
    @Api(2.3)
    public
    @BatchOperator.Operation
    String getOperation() {
        return mOperation;
    }
}
