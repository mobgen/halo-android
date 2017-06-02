package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.internal.objectmappers.ObjectMapper;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by f.souto.gonzalez on 02/06/2017.
 */
@JsonObject
public class HaloEvent implements Parcelable {

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

    @JsonField(name = "type")
    String mType;

    @JsonField(name = "coord")
    String mCoord;

    @JsonField(name = "extra", typeConverter = JSONObjectConverter.class)
    JSONObject mExtra;

    @JsonField(name = "ip")
    String mIp;

    @JsonField(name = "category")
    String mCategory;

    @JsonField(name = "customerId")
    int mCustomerId;

    @JsonField(name = "appId")
    int mAppId;

    //default constructor
    protected HaloEvent() {
    }

    private HaloEvent(@NonNull Builder builder) {
        mType = builder.mType;
        mExtra = builder.mExtra;
        mCoord = builder.mCoord;
    }


    protected HaloEvent(Parcel in) {
        mType = in.readString();
        mCoord = in.readString();
        mIp = in.readString();
        mCategory = in.readString();
        mCustomerId = in.readInt();
        mAppId = in.readInt();
        try {
            String values = in.readString();
            this.mExtra = values != null ? new JSONObject(values) : null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The values of the item " + mExtra + "could not be parsed on the parceling op.");
        }
    }

    public static final Creator<HaloEvent> CREATOR = new Creator<HaloEvent>() {
        @Override
        public HaloEvent createFromParcel(Parcel in) {
            return new HaloEvent(in);
        }

        @Override
        public HaloEvent[] newArray(int size) {
            return new HaloEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mCoord);
        dest.writeString(mIp);
        dest.writeString(mCategory);
        dest.writeInt(mCustomerId);
        dest.writeInt(mAppId);
        dest.writeString(mExtra != null ? this.mExtra.toString() : null);
    }

    /**
     * Creates a new halo tracking event builder.
     *
     * @return The halo event tracking builder.
     */
    @NonNull
    @Api(2.33)
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<HaloEvent> {

        private String mType;

        private String mCoord;

        private JSONObject mExtra;

        /**
         * The builder constructor.
         */
        private Builder() {

        }


        @NonNull
        @Api(2.33)
        public HaloEvent.Builder withType(@NonNull String type) {
            mType = type;
            return this;
        }

        @NonNull
        @Api(2.33)
        public HaloEvent.Builder withLocation(@NonNull String coord) {
            mCoord = coord;
            return this;
        }

        /**
         * Set the content extra values.
         *
         * @param values
         * @return
         */
        public HaloEvent.Builder withExtra(@NonNull Object values) {
            AssertionUtils.notNull(values, "values");
            Map<String, Object> mapped = mapValues(values);
            if (mapped != null) {
                JSONObject data = new JSONObject(mapped);
                mExtra = data;
            }
            return this;
        }

        /**
         * Map values from model object.
         *
         * @param object
         * @return The mapped values or null
         */
        @Nullable
        private Map<String, Object> mapValues(Object object) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.parseMap(mapper.serialize(object));
                return map;
            } catch (IOException e) {
                return null;
            }
        }


        @NonNull
        @Override
        public HaloEvent build() {
            return new HaloEvent(this);
        }
    }

    /**
     * Serialize a instace.
     *
     * @param haloEvent The halo event to track.
     * @param parser    The parser.
     * @return The instance serilialized.
     * @throws HaloParsingException
     */
    public static String serialize(@NonNull HaloEvent haloEvent, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloEvent, "haloEvent");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<HaloEvent, String>) parser.serialize(HaloEvent.class)).convert(haloEvent);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the haloEvent", e);
        }
    }
}
