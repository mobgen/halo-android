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
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.sdk.api.Halo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fernandosouto on 19/04/17.
 */
@Keep
@JsonObject
public class BatchErrorInfo implements Parcelable {

    /**
     * Flag to check for conflict status on batch operations.
     */
    public static final int CONFLICT_STATUS = 409;

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
            if (object != null) {
                jsonGenerator.writeFieldName(fieldName);
                jsonGenerator.writeRaw(":" + object.toString());
            }
        }
    }

    @JsonField(name = "status")
    Integer mStatus;

    @JsonField(name = "message")
    String mMessage;

    @JsonField(name = "type")
    String mType;

    @JsonField(name = "extra", typeConverter = HaloContentInstance.JSONObjectConverter.class)
    JSONObject mExtra;

    protected BatchErrorInfo() {

    }

    public static final Creator<BatchErrorInfo> CREATOR = new Creator<BatchErrorInfo>() {
        @Override
        public BatchErrorInfo createFromParcel(Parcel in) {
            return new BatchErrorInfo(in);
        }

        @Override
        public BatchErrorInfo[] newArray(int size) {
            return new BatchErrorInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected BatchErrorInfo(Parcel in) {
        this.mStatus = in.readInt();
        this.mMessage = in.readString();
        this.mType = in.readString();
        try {
            String extra = in.readString();
            this.mExtra = extra != null ? new JSONObject(extra) : null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The values of " + mExtra + "could not be parsed on the parceling op.");
        }
    }

    /**
     * Get the status of the error.
     *
     * @return The status of the error.
     */
    @Api(2.3)
    public Integer getStatus() {
        return mStatus;
    }

    /**
     * Get the message of the error.
     *
     * @return The message of the error.
     */
    @Nullable
    @Api(2.3)
    public String getMessage() {
        return mMessage;
    }

    /**
     * Get the type of the error.
     *
     * @return The type of the error
     */
    @NonNull
    @Api(2.3)
    public String getType() {
        return mType;
    }

    /**
     * Get the extra values as raw.
     *
     * @return The extra values of the error.
     */
    @Nullable
    @Api(2.3)
    public JSONObject getExtra() {
        return mExtra;
    }

    /**
     * Get the data of the conflict instance.
     *
     * @return The data parsed as HaloContentInstance or null
     */
    @Api(2.3)
    @Nullable
    public HaloContentInstance getExtraInstance() {
        try {
            if (mStatus == CONFLICT_STATUS) {
                return HaloContentInstance.deserialize(mExtra.toString(), Halo.instance().framework().parser());
            }
        } catch (HaloParsingException parsingExcetion) {
            return null;
        }
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatus);
        dest.writeString(this.mMessage);
        dest.writeString(this.mType);
        dest.writeString(mExtra != null ? this.mExtra.toString() : null);

    }
}
