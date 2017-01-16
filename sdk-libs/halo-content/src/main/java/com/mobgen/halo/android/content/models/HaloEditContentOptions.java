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
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The general content element. This element allows to bring data from a general content middleware and transform
 * it to another model data.
 */
@Keep
@JsonObject
public class HaloEditContentOptions implements Parcelable {

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
            Map map = (Map) mapper.parse(jsonParser);
            return map != null ? new JSONObject(map) : null;
        }

        @Override
        public void serialize(JSONObject object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeRaw(":"+object.toString());
        }
    }

    /**
     * The item id.
     */
    @JsonField(name = "id")
    String mItemId;

    /**
     * The module name.
     */
    String mModuleName;

    /**
     * The external id of this middleware.
     */
    @JsonField(name = "module")
    String mModuleId;

    /**
     * The name of this element.
     */
    @JsonField(name = "name")
    String mName;

    /**
     * The values field.
     */
    @JsonField(name = "values", typeConverter = JSONObjectConverter.class)
    JSONObject mValues;

    /**
     * The tags of this element.
     */
    @JsonField(name = "tags")
    List<HaloSegmentationTag> mTags;

    /**
     * Publish date for the general content instance.
     */
    @JsonField(name = "publishedAt" )
    Date mPublishedAt;

    /**
     * Date when this element has been removed.
     */
    @JsonField(name = "removedAt")
    Date mRemovedAt;

    /**
     * Publish date for the general content instance.
     */
    @JsonField(name = "archivedAt")
    Date mArchived;

    /**
     * The creator for parcelable.
     */
    public static final Creator<HaloEditContentOptions> CREATOR = new Creator<HaloEditContentOptions>() {
        public HaloEditContentOptions createFromParcel(Parcel source) {
            return new HaloEditContentOptions(source);
        }

        public HaloEditContentOptions[] newArray(int size) {
            return new HaloEditContentOptions[size];
        }
    };

    /**
     * Constructor for the general content instance.
     */
    public HaloEditContentOptions() {
        //Constructor for the class.
    }

    /**
     * Constructor for the general content instance.
     *
     * @param itemId          The item id.
     * @param moduleName      The module name.
     * @param moduleId        The module id.
     * @param name            The name.
     * @param values          The values.
     * @param tags          The author of this item.
     * @param archivedDate     The creation date.
     * @param publicationDate The published date.
     * @param deletionDate    The removed date.
     */
    @Keep
    @Api(2.2)
    public HaloEditContentOptions(String itemId, String moduleName, String moduleId, String name, JSONObject values, Date archivedDate, Date publicationDate, Date deletionDate, List<HaloSegmentationTag> tags) {
        mItemId = itemId;
        mModuleId = moduleId;
        mModuleName = moduleName;
        mName = name;
        mValues = values;
        mTags = tags;
        mArchived = archivedDate;
        mPublishedAt = publicationDate;
        mRemovedAt = deletionDate;
    }

    protected HaloEditContentOptions(Parcel in) {
        this.mItemId = in.readString();
        this.mModuleName = in.readString();
        this.mModuleId = in.readString();
        this.mName = in.readString();
        try {
            String values = in.readString();
            this.mValues = values != null ? new JSONObject(values): null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The values of the general content item " + mItemId + "could not be parsed on the parceling op.");
        }
        this.mTags = in.createTypedArrayList(HaloSegmentationTag.CREATOR);
        long tmpMarchivedDate = in.readLong();
        this.mArchived = tmpMarchivedDate == -1 ? null : new Date(tmpMarchivedDate);
        long tmpMPublishedAt = in.readLong();
        this.mPublishedAt = tmpMPublishedAt == -1 ? null : new Date(tmpMPublishedAt);
        long tmpMRemovedAt = in.readLong();
        this.mRemovedAt = tmpMRemovedAt == -1 ? null : new Date(tmpMRemovedAt);
    }

    /**
     * Provides the middleware name.
     *
     * @return The middleware name.
     */
    @Keep
    @Api(2.2)
    public String getModuleName() {
        return mModuleName;
    }

    /**
     * Provides the middleware id.
     *
     * @return The middleware id.
     */
    @Keep
    @Api(2.2)
    public String getModuleId() {
        return mModuleId;
    }

    /**
     * Provides the item id.
     *
     * @return The item id.
     */
    @Keep
    @Api(2.2)
    public String getItemId() {
        return mItemId;
    }

    /**
     * Provides the name of the middleware.
     *
     * @return The name of the middleware.
     */
    @Keep
    @Api(2.2)
    public String getName() {
        return mName;
    }

    /**
     * Provides the custom values of this item.
     *
     * @return The custom values of this item.
     */
    @Keep
    @Api(2.2)
    public JSONObject getValues() {
        return mValues;
    }

    /**
     * Provides the tags of this item.
     *
     * @return The author of this middleware item.
     */
    @Keep
    @Api(2.2)
    public List<HaloSegmentationTag> getTags() {
        return mTags;
    }

    /**
     * Provides the archived date of this item.
     *
     * @return The middleware item update date.
     */
    @Keep
    @Api(2.2)
    public Date getArchivedDate() {
        return mArchived;
    }

    /**
     * Provides the date when this object was published at.
     *
     * @return The date of publication.
     */
    @Keep
    @Api(2.2)
    public Date getPublishedDate() {
        return mPublishedAt;
    }

    /**
     * Provides the removal date of this item.
     *
     * @return The removal date of this item.
     */
    @Keep
    @Api(2.2)
    public Date getRemoveDate() {
        return mRemovedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mItemId);
        dest.writeString(this.mModuleName);
        dest.writeString(this.mModuleId);
        dest.writeString(this.mName);
        dest.writeString(mValues != null ? this.mValues.toString() : null);
        dest.writeTypedList(mTags);
        dest.writeLong(mArchived != null ? mArchived.getTime() : -1);
        dest.writeLong(mPublishedAt != null ? mPublishedAt.getTime() : -1);
        dest.writeLong(mRemovedAt != null ? mRemovedAt.getTime() : -1);
    }

    @Override
    public String toString() {
        return "ContentInstance{" +
                "mItemId='" + mItemId + '\'' +
                ", mModuleName='" + mModuleName + '\'' +
                ", mModuleId='" + mModuleId + '\'' +
                ", mName='" + mName + '\'' +
                ", mValues=" + mValues +
                ", mTags=" + mTags.toString() +
                ", mArchivedAt=" + mArchived +
                ", mPublishedAt=" + mPublishedAt +
                ", mRemovedAt=" + mRemovedAt +
                '}';
    }

    /**
     * Provides the serializer given the factory.
     *
     * @param haloContentInstance The object to serialize.
     * @param parser   The parser factory.
     * @return The parser obtained.
     */
    public static String serialize(@NonNull HaloEditContentOptions haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<HaloEditContentOptions, String>) parser.serialize(HaloEditContentOptions.class)).convert(haloContentInstance);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the HaloEditContentOptions", e);
        }
    }

    /**
     * Parses a Halo content instance.
     *
     * @param haloContentInstance   The haloContentInstance as string.
     * @param parser The parser.
     * @return The haloContentInstance parsed or an empty haloContentInstance if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static HaloEditContentOptions deserialize(@Nullable String haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (haloContentInstance != null) {
            try {
                return ((Parser<InputStream, HaloEditContentOptions>) parser.deserialize(HaloEditContentOptions.class)).convert(new ByteArrayInputStream(haloContentInstance.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the halocontentInstance", e);
            }
        }
        return null;
    }

    /**
     * The Halo edit content instance.
     *
     * @param builder The builder.
     */
    private HaloEditContentOptions(@NonNull Builder builder) {
        mItemId = builder.mItemId;
        mModuleName = builder.mModuleName;
        mModuleId = builder.mModuleId;
        mName = builder.mName;
        mTags = builder.mTags;
        mValues = builder.mValues;
        mArchived = builder.mArchivedAt;
        mPublishedAt = builder.mPublishedAt;
        mRemovedAt = builder.mRemovedAt;
    }

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<HaloEditContentOptions> {

        /**
         * The item id.
         */
        String mItemId;

        /**
         * The external id of this middleware.
         */
        String mModuleId;

        /**
         * The name of this element.
         */
        String mName;

        /**
         * The tags of this element.
         */
        List<HaloSegmentationTag> mTags;
        /**
         * The values field.
         */
        JSONObject mValues;

        /**
         * Last updated date.
         */
        Date mArchivedAt;

        /**
         * Publish date for the general content instance.
         */
        Date mPublishedAt;

        /**
         * Date when this element has been removed.
         */
        Date mRemovedAt;
        /**
         * The module name
         */
        private String mModuleName;
        /**
         * Creates the builder
         *
         */
        public Builder(@NonNull String moduleName) {
            mModuleName = moduleName;
        }

        /**
         * Set the item id to use on update or delete operations.
         *
         * @param id
         * @return The builder
         */
        @NonNull
        public Builder withId(@NonNull String id){
            AssertionUtils.notNull(id, "id");
            mItemId = id;
            return this;
        }

        /**
         * Set the module id.
         *
         * @param moduleId
         * @return
         */
        public Builder withModuleId(@NonNull String moduleId){
            AssertionUtils.notNull(moduleId, "moduleId");
            mModuleId = moduleId;
            return this;
        }

        /**
         * Set the name of the content item.
         *
         * @param name
         * @return
         */
        public Builder withName(@NonNull String name){
            AssertionUtils.notNull(name, "name");
            mName = name;
            return this;
        }

        /**
         * Set the segmentation tags to the content instance
         *
         * @param tags
         * @return
         */
        public Builder withTags(@NonNull List<HaloSegmentationTag> tags){
            AssertionUtils.notNull(tags, "tags");
            mTags = tags;
            return this;
        }

        /**
         * Set the content values.
         * @param values
         * @return
         */
        public Builder withContentData(@NonNull Object values) {
            AssertionUtils.notNull(values, "values");
            Map<String,Object> mapped = mapValues(values);
            if(mapped!=null) {
                JSONObject data = new JSONObject(mapped);
                mValues = data;
            }
            return this;
        }

        /**
         * Map values from model object.
         * @param object
         * @return The mapped values or null
         */
        @Nullable
        private Map<String,Object> mapValues(Object object){
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.parseMap(mapper.serialize(object));
                return map;
            } catch (IOException e) {
                return null;
            }
        }

        /**
         * Set the publish date.
         * @param publishDate
         * @return
         */
        public Builder withPublishDate(@NonNull Date publishDate){
            AssertionUtils.notNull(publishDate, "publishDate");
            mPublishedAt = publishDate;
            return this;
        }

        /**
         * Set the removal date shceduled.
         * @param removalDate
         * @return
         */
        public Builder withRemovalDate(@NonNull Date removalDate){
            AssertionUtils.notNull(removalDate, "removalDate");
            mRemovedAt = removalDate;
            return this;
        }

        @NonNull
        @Override
        public HaloEditContentOptions build() {
            return new HaloEditContentOptions(this);
        }
    }
}
