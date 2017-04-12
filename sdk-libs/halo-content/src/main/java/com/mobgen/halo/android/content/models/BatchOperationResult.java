package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fernandosouto on 05/04/17.
 */

/**
 * Result from remote data source for a batch operation.
 */
@JsonObject
public class BatchOperationResult implements Parcelable {

    /**
     * Json converter to transform the json that comes to a json object.
     */
    @Keep
    public static class JSONObjectConverter implements TypeConverter<JSONObject> {

        /**
         * The mapper for objects.
         */
        private static final JsonMapper<Object> mapper = LoganSquare.mapperFor(Object.class);

        @Override
        public JSONObject parse(JsonParser jsonParser) throws IOException {
            //convert map to json string to create the object due to problems on pre 4.3 devices
            //See https://mobgen.atlassian.net/browse/HALO-2918
            Map map = (Map) mapper.parse(jsonParser);
            try {
                return new JSONObject(LoganSquare.serialize(map));
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public void serialize(JSONObject object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeRaw(":" + object.toString());
        }
    }

    @JsonField(name = "operation")
    String mOperation;
    @JsonField(name = "position")
    int mPosition;
    @JsonField(name = "success")
    boolean mSuccess;
    @JsonField(name = "data", typeConverter = BatchOperationResult.JSONObjectConverter.class)
    JSONObject mData;

    public BatchOperationResult() {

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
        try {
            String data = in.readString();
            this.mData = data != null ? new JSONObject(data): null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The values of the general content item " + mPosition + "could not be parsed on the parceling op.");
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOperation);
        dest.writeInt(this.mPosition);
        dest.writeByte((byte) (mSuccess ? 1 : 0));
        dest.writeString(mData != null ? this.mData.toString() : null);
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
    public JSONObject getData() {
        return mData;
    }

    /**
     * Get the operation that was performed.
     *
     * @return The operation
     */
    @NonNull
    @Api(2.3)
    public @BatchOperator.Operation String getOperation() {
        return mOperation;
    }
}
