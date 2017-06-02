package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * Created by f.souto.gonzalez on 02/06/2017.
 */

/**
 * Halo events to track user analytics.You may select one of predefined types when creating an instance.
 *
 */
@Keep
@JsonObject
public class HaloEvent implements Parcelable {

    /**
     * Halo supported events.
     */
    @StringDef({OPEN_APPLICATION, REGISTER_DEVICE, UPDATE_DEVICE, REGISTER_LOCATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {
    }

    /**
     * Event when user open application.
     */
    public static final String OPEN_APPLICATION = "1";
    /**
     * Event when user register a device.
     */
    public static final String REGISTER_DEVICE = "2";
    /**
     * Event when user update device.
     */
    public static final String UPDATE_DEVICE = "3";
    /**
     * Event when user register a location status.
     */
    public static final String REGISTER_LOCATION = "4";


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
                jsonGenerator.writeRawValue(object.toString());
            }
        }
    }

    /**
     * The type of the event.
     */
    @JsonField(name = "type")
    String mType;

    /**
     * The location of the event.
     */
    @JsonField(name = "coord")
    String mCoord;

    /**
     * An object with additional information of the event.
     */
    @JsonField(name = "extra", typeConverter = JSONObjectConverter.class)
    JSONObject mExtra;

    /**
     * The ip address of the event.
     */
    @JsonField(name = "ip")
    String mIp;

    /**
     * The category of the event.
     */
    @JsonField(name = "category")
    String mCategory;


    /**
     *  Default constructor
     */
    protected HaloEvent() {
    }

    private HaloEvent(@NonNull Builder builder) {
        mType = builder.mType;
        mExtra = builder.mExtra;
        mCoord = builder.mCoord;
    }

    /**
     * Get the type of the event.
     * @return The type of the event.
     */
    @NonNull
    @Api(2.33)
    public String getType() {
        return mType;
    }

    /**
     * Get the location of the event.
     *
     * @return The location of the event.
     */
    @NonNull
    @Api(2.33)
    public String getCoord() {
        return mCoord;
    }

    /**
     * Get the extra data of the event.
     *
     * @return The additional info of the event.
     */
    @NonNull
    @Api(2.33)
    public JSONObject getExtra() {
        return mExtra;
    }

    /**
     * Get the ip of the event.
     *
     * @return The ip address of the event.
     */
    @NonNull
    @Api(2.33)
    public String getIp() {
        return mIp;
    }

    /**
     * Get the category of the event.
     *
     * @return The category of the event.
     */
    @NonNull
    @Api(2.33)
    public String getCategory() {
        return mCategory;
    }

    protected HaloEvent(Parcel in) {
        mType = in.readString();
        mCoord = in.readString();
        mIp = in.readString();
        mCategory = in.readString();
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

        /**
         * The type of the event.
         */
        private String mType;

        /**
         * The location of the event.
         */
        private String mCoord;

        /**
         * The additional information of the event.
         */
        private JSONObject mExtra;

        /**
         * The builder constructor.
         */
        private Builder() {

        }

        /**
         * Set the type of the event.
         * @param type The type of the event.
         * @return The builder.
         */
        @NonNull
        @Api(2.33)
        public HaloEvent.Builder withType(@EventType String type) {
            mType = type;
            return this;
        }

        /**
         * Set the location of the event.
         * @param coord The location of the event.
         * @return The builder.
         */
        @NonNull
        @Api(2.33)
        public HaloEvent.Builder withLocation(@NonNull String coord) {
            mCoord = coord;
            return this;
        }

        /**
         * Set the content extra values.
         *
         * @param values The additional information.
         * @return The builder.
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
         * @param object The object to map.
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

        /**
         * Build the instance.
         * @return The HaloEvent instance.
         */
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
