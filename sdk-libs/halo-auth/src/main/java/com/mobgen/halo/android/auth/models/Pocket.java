package com.mobgen.halo.android.auth.models;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */
@Keep
@JsonObject
public class Pocket implements Parcelable {
    //TODO PARCELABLE

    /**
     * Json converter to transform the json that comes to a json object.
     */
    @Keep
    public static class JSONDataObjectConverter implements TypeConverter<JSONObject> {

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

    /**
     * Json converter to transform the json that comes to a json object.
     */
    @Keep
    public static class JSONReferencesObjectConverter implements TypeConverter<List<JSONObject>> {

        /**
         * The mapper for objects.
         */
        private static final JsonMapper<Object> mapper = LoganSquare.mapperFor(Object.class);

        @Override
        public List<JSONObject> parse(JsonParser jsonParser) throws IOException {
            Map map = (Map) mapper.parse(jsonParser);
            Object[] keysetNames = map.keySet().toArray();
            Object[] referenceContainer = new Object[keysetNames.length];
            for (int i = 0; i < keysetNames.length; i ++){
                List<String> referenceData = (List<String>) map.get(keysetNames[i]);
                referenceContainer[i] = new ReferenceContainer((String)keysetNames[i],referenceData);
            }
            List<JSONObject> referenceContainerList = new ArrayList<>();
            referenceContainerList = addToList(referenceContainerList,referenceContainer);
            return referenceContainerList;
        }

        private Map<String, Object> mapValues(Object object) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.parseMap(mapper.serialize(object));
                return map;
            } catch (IOException e) {
                return null;
            }
        }

        public List<JSONObject> addToList(@Nullable List<JSONObject> list, @Nullable Object[] items) {
            List<JSONObject> finalList = list;
            if (items != null && items.length > 0) {
                if (finalList == null) {
                    finalList = new ArrayList<>();
                }
                for (int i = 0; i < items.length; i++) {
                    Map<String, Object> mapped = mapValues(items[i]);
                    if (mapped != null) {
                        JSONObject data = new JSONObject(mapped);
                        finalList.add(data);
                    }
                }
            }
            return finalList;
        }

        @Override
        public void serialize(List<JSONObject> object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {

            if (object != null) {
                String referenceContainer = "";
                for(int j = 0; j < object.size(); j++) {
                    try {
                        referenceContainer = referenceContainer + "\"" + object.get(j).get("referenceName").toString() + "\"";
                        if (object.get(j).get("referenceData") != null) {
                            referenceContainer =  referenceContainer  + ":" + object.get(j).get("referenceData").toString();
                        } else {
                            referenceContainer = referenceContainer + ":null";
                        }
                        if(j < object.size() -1) {
                            referenceContainer = referenceContainer + ",";
                        }
                    } catch (JSONException e) {
                    }
                }
                //jsonGenerator.writeFieldName(fieldName);
                jsonGenerator.writeRaw(",\"" + fieldName + "\"" + ":{"+ referenceContainer + "}");
            }
        }
    }

    @JsonField(name = "references", typeConverter = JSONReferencesObjectConverter.class)
    List<JSONObject> mReferences;

    @JsonField(name = "data", typeConverter = JSONDataObjectConverter.class)
    JSONObject mData;

    protected Pocket() {
        //empty constructor
    }

    /**
     * The Halo edit content instance.
     *
     * @param builder The builder.
     */
    private Pocket(@NonNull Pocket.Builder builder) {
        mData = builder.mData;
        mReferences = builder.mReferences;
    }

    protected Pocket(Parcel in) {
        mReferences = in.readParcelable(ReferenceContainer.class.getClassLoader());
        try {
            String values = in.readString();
            this.mData = values != null ? new JSONObject(values) : null;
        } catch (JSONException e) {
            Halog.e(getClass(), "Cannot convert mData");
        }
    }

    public static final Creator<Pocket> CREATOR = new Creator<Pocket>() {
        @Override
        public Pocket createFromParcel(Parcel in) {
            return new Pocket(in);
        }

        @Override
        public Pocket[] newArray(int size) {
            return new Pocket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeParcelable(this.mReferences, flags);
        dest.writeString(mData != null ? this.mData.toString() : null);
    }


    /**
     * Provides the custom values of this item.
     *
     * @return The custom values of this item.
     */
    @Keep
    @Api(2.4)
    public List<JSONObject> getReferences() {
        return mReferences;
    }

    /**
     * Provides the custom values of this item.
     *
     * @return The custom values of this item.
     */
    @Keep
    @Api(2.4)
    public JSONObject getValues() {
        return mData;
    }

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<Pocket> {

        /**
         * The reference container.
         */
        List<JSONObject> mReferences;

        /**
         * The custom user data.
         */
        JSONObject mData;


        /**
         * Creates the builder.
         */
        public Builder() {

        }

        /**
         * Set the references.
         *
         * @param references The references to save.
         * @return
         */
        @NonNull
        @Api(2.4)
        public Pocket.Builder withReferences(@NonNull ReferenceContainer... references) {
            AssertionUtils.notNull(references, "references");
            mReferences = addToList(mReferences, references);
            return this;
        }

        /**
         * Set the custom data values.
         *
         * @param values The custom data.
         * @return
         */
        @NonNull
        @Api(2.4)
        public Pocket.Builder withData(@NonNull Object values) {
            AssertionUtils.notNull(values, "values");
            Map<String, Object> mapped = mapValues(values);
            if (mapped != null) {
                JSONObject data = new JSONObject(mapped);
                mData = data;
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


        /**
         * Adds something to the given list or creates it returning as a result.
         *
         * @param list  The list of items.
         * @param items The items.
         * @return The list returned or created.
         */
        @NonNull
        public List<JSONObject> addToList(@Nullable List<JSONObject> list, @Nullable Object[] items) {
            List<JSONObject> finalList = list;
            if (items != null && items.length > 0) {
                if (finalList == null) {
                    finalList = new ArrayList<>();
                }
                for (int i = 0; i < items.length; i++) {
                    Map<String, Object> mapped = mapValues(items[i]);
                    if (mapped != null) {
                        JSONObject data = new JSONObject(mapped);
                        finalList.add(data);
                    }
                }
            }
            return finalList;
        }

        @NonNull
        @Override
        public Pocket build() {
            return new Pocket(this);
        }
    }

    /**
     * Provides the serializer given the factory.
     *
     * @param pocket The object to serialize.
     * @param parser The parser factory.
     * @return The parser obtained.
     */
    public static String serialize(@NonNull Pocket pocket, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(pocket, "pocket");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<Pocket, String>) parser.serialize(Pocket.class)).convert(pocket);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the HaloContentInstance", e);
        }
    }
}
