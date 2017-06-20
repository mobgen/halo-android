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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

/**
 * The pocket class to store custom identify information and string references.
 */
@Keep
@JsonObject
public class Pocket implements Parcelable {
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
                jsonGenerator.writeRawValue(object.toString());
            }
        }
    }

    /**
     * Json converter to transform the json that comes to a json object.
     */
    @Keep
    public static class JSONReferencesObjectConverter implements TypeConverter<List<ReferenceContainer>> {

        /**
         * The mapper for objects.
         */
        private static final JsonMapper<Object> mapper = LoganSquare.mapperFor(Object.class);

        @Override
        public List<ReferenceContainer> parse(JsonParser jsonParser) throws IOException {
            Map map = (Map) mapper.parse(jsonParser);
            Object[] keysetNames = map.keySet().toArray();
            List<ReferenceContainer> referenceContainerList = new ArrayList<>();
            for (int i = 0; i < keysetNames.length; i++) {
                List<String> referenceData = (List<String>) map.get(keysetNames[i]);
                ReferenceContainer refContainer = new ReferenceContainer.Builder((String) keysetNames[i])
                        .references(referenceData.toArray(new String[referenceData.size()]))
                        .build();
                referenceContainerList.add(refContainer);
            }
            return referenceContainerList;
        }

        @Override
        public void serialize(List<ReferenceContainer> object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            if (object != null) {
                String referenceContainer = "";
                for (int j = 0; j < object.size(); j++) {
                    if (object.get(j).getReferences() != null) {
                        referenceContainer = referenceContainer + mapper.serialize(object.get(j).getName().toString());
                        referenceContainer = referenceContainer + ": [";
                        for (int k = 0; k < object.get(j).getReferences().size(); k++) {
                            referenceContainer = referenceContainer + mapper.serialize(object.get(j).getReferences().get(k));
                            if (k < object.get(j).getReferences().size() - 1) {
                                referenceContainer = referenceContainer + ",";
                            }
                        }
                        referenceContainer = referenceContainer + "]";
                    }
                    if (j < object.size() - 1) {
                        referenceContainer = referenceContainer + ",";
                    }
                }
                jsonGenerator.writeFieldName(fieldName);
                jsonGenerator.writeRawValue("{" + referenceContainer + "}");
            }
        }
    }

    @JsonField(name = "references", typeConverter = JSONReferencesObjectConverter.class)
    List<ReferenceContainer> mReferences;

    @JsonField(name = "cdummy")
    String myDummy;

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
        mReferences = builder.mReferences;
        mData = builder.mData;
    }

    protected Pocket(Parcel in) {
        mReferences = in.createTypedArrayList(ReferenceContainer.CREATOR);
        try {
            String values = in.readString();
            this.mData = values != null ? new JSONObject(values) : null;
        } catch (JSONException e) {
            Halog.e(getClass(), "Cannot convert mData");
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mReferences);
        dest.writeString(mData != null ? this.mData.toString() : null);
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


    /**
     * Provides the custom values of this item.
     *
     * @return The custom values of this item.
     */
    @Keep
    @Api(2.4)
    public List<ReferenceContainer> getReferences() {
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
     * Provides the custom values of this item.
     *
     * @param clazz The class to convert the parsed data.
     * @return The custom values of this item.
     */
    @Keep
    @Api(2.4)
    @Nullable
    public <T> T getValues(@NonNull Class<T> clazz, @NonNull Parser.Factory parser) {
        if (mData != null) {
            try {
                return ((Parser<InputStream, T>) parser.deserialize(clazz)).convert(new ByteArrayInputStream(mData.toString().getBytes()));
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<Pocket> {

        /**
         * The reference container.
         */
        List<ReferenceContainer> mReferences;

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
            throw new HaloParsingException("Error while serializing the Pocket", e);
        }
    }

    /**
     * Map values from model object.
     *
     * @param object
     * @return The mapped values or null
     */
    @Nullable
    public static Map<String, Object> mapValues(Object object) {
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
    public static List<ReferenceContainer> addToList(@Nullable List<ReferenceContainer> list, @Nullable ReferenceContainer[] items) {
        List<ReferenceContainer> finalList = list;
        if (items != null && items.length > 0) {
            if (finalList == null) {
                finalList = new ArrayList<>();
            }
            for (int i = 0; i < items.length; i++) {
                finalList.add(items[i]);
            }
        }
        return finalList;
    }
}
