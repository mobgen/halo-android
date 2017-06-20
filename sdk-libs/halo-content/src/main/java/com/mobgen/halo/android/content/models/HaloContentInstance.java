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
public class HaloContentInstance implements Parcelable {

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
     * The name of the author who created this element.
     */
    @JsonField(name = "createdBy")
    String mAuthor;

    /**
     * Creation date of this element on the Halo.
     */
    @JsonField(name = "createdAt")
    Date mCreatedDate;

    /**
     * Last updated date.
     */
    @JsonField(name = "updatedAt")
    Date mLastUpdate;

    /**
     * Publish date for the general content instance.
     */
    @JsonField(name = "publishedAt")
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
    public static final Creator<HaloContentInstance> CREATOR = new Creator<HaloContentInstance>() {
        public HaloContentInstance createFromParcel(Parcel source) {
            return new HaloContentInstance(source);
        }

        public HaloContentInstance[] newArray(int size) {
            return new HaloContentInstance[size];
        }
    };

    /**
     * Constructor for the general content instance.
     */
    public HaloContentInstance() {
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
     * @param tags            The author of this item.
     * @param author          The author of this item.
     * @param createdDate     The creation date.
     * @param archivedDate    The archived date.
     * @param updateDate      The last updated date.
     * @param publicationDate The published date.
     * @param deletionDate    The removed date.
     */
    @Keep
    @Api(2.2)
    public HaloContentInstance(String itemId, String moduleName, String moduleId, String name, JSONObject values, String author, Date archivedDate, Date createdDate, Date updateDate, Date publicationDate, Date deletionDate, List<HaloSegmentationTag> tags) {
        mItemId = itemId;
        mModuleId = moduleId;
        mModuleName = moduleName;
        mName = name;
        mValues = values;
        mAuthor = author;
        mTags = tags;
        mCreatedDate = createdDate;
        mLastUpdate = updateDate;
        mArchived = archivedDate;
        mPublishedAt = publicationDate;
        mRemovedAt = deletionDate;
    }

    protected HaloContentInstance(Parcel in) {
        this.mItemId = in.readString();
        this.mModuleName = in.readString();
        this.mModuleId = in.readString();
        this.mName = in.readString();
        try {
            String values = in.readString();
            this.mValues = values != null ? new JSONObject(values) : null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The values of the general content item " + mItemId + "could not be parsed on the parceling op.");
        }
        this.mTags = in.createTypedArrayList(HaloSegmentationTag.CREATOR);
        this.mAuthor = in.readString();
        long tmpMarchivedDate = in.readLong();
        this.mArchived = tmpMarchivedDate == -1 ? null : new Date(tmpMarchivedDate);
        long tmpMCreatedDate = in.readLong();
        this.mCreatedDate = tmpMCreatedDate == -1 ? null : new Date(tmpMCreatedDate);
        long tmpMLastUpdate = in.readLong();
        this.mLastUpdate = tmpMLastUpdate == -1 ? null : new Date(tmpMLastUpdate);
        long tmpMPublishedAt = in.readLong();
        this.mPublishedAt = tmpMPublishedAt == -1 ? null : new Date(tmpMPublishedAt);
        long tmpMRemovedAt = in.readLong();
        this.mRemovedAt = tmpMRemovedAt == -1 ? null : new Date(tmpMRemovedAt);
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
        dest.writeString(this.mAuthor);
        dest.writeLong(mArchived != null ? mArchived.getTime() : -1);
        dest.writeLong(mCreatedDate != null ? mCreatedDate.getTime() : -1);
        dest.writeLong(mLastUpdate != null ? mLastUpdate.getTime() : -1);
        dest.writeLong(mPublishedAt != null ? mPublishedAt.getTime() : -1);
        dest.writeLong(mRemovedAt != null ? mRemovedAt.getTime() : -1);
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
    @Api(2.0)
    public String getModuleId() {
        return mModuleId;
    }

    /**
     * Provides the item id.
     *
     * @return The item id.
     */
    @Keep
    @Api(2.0)
    public String getItemId() {
        return mItemId;
    }

    /**
     * Provides the name of the middleware.
     *
     * @return The name of the middleware.
     */
    @Keep
    @Api(2.0)
    public String getName() {
        return mName;
    }

    /**
     * Provides the custom values of this item.
     *
     * @return The custom values of this item.
     */
    @Keep
    @Api(2.0)
    public JSONObject getValues() {
        return mValues;
    }

    /**
     * Provides the author of this middleware item.
     *
     * @return The author of this middleware item.
     */
    @Keep
    @Api(2.0)
    public String getAuthor() {
        return mAuthor;
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
     * Provides the creation date of this middleware item.
     *
     * @return The middleware item creation date.
     */
    @Keep
    @Api(2.0)
    public Date getCreatedDate() {
        return mCreatedDate;
    }

    /**
     * Provides the update date of this middleware item.
     *
     * @return The middleware item update date.
     */
    @Keep
    @Api(2.0)
    public Date getLastUpdate() {
        return mLastUpdate;
    }

    /**
     * Provides the date when this object was published at.
     *
     * @return The date of publication.
     */
    @Keep
    @Api(2.0)
    public Date getPublishedDate() {
        return mPublishedAt;
    }

    /**
     * Provides the removal date of this item.
     *
     * @return The removal date of this item.
     */
    @Keep
    @Api(2.0)
    public Date getRemoveDate() {
        return mRemovedAt;
    }

    @Override
    public String toString() {
        String tags = mTags != null ? mTags.toString() : null;
        return "ContentInstance{" +
                "mItemId='" + mItemId + '\'' +
                ", mModuleName='" + mModuleName + '\'' +
                ", mModuleId='" + mModuleId + '\'' +
                ", mName='" + mName + '\'' +
                ", mValues=" + mValues +
                ", mAuthor='" + mAuthor + '\'' +
                ", mTags=" + tags +
                ", mCreatedDate=" + mCreatedDate +
                ", mArchivedAt=" + mArchived +
                ", mLastUpdate=" + mLastUpdate +
                ", mPublishedAt=" + mPublishedAt +
                ", mRemovedAt=" + mRemovedAt +
                '}';
    }


    /**
     * Provides the serializer given the factory.
     *
     * @param haloContentInstance The object to serialize.
     * @param parser              The parser factory.
     * @return The parser obtained.
     */
    public static String serialize(@NonNull HaloContentInstance haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<HaloContentInstance, String>) parser.serialize(HaloContentInstance.class)).convert(haloContentInstance);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the HaloContentInstance", e);
        }
    }

    /**
     * Parses a Halo content instance.
     *
     * @param haloContentInstance The haloContentInstance as string.
     * @param parser              The parser.
     * @return The haloContentInstance parsed or an empty haloContentInstance if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static HaloContentInstance deserialize(@Nullable String haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (haloContentInstance != null) {
            try {
                return ((Parser<InputStream, HaloContentInstance>) parser.deserialize(HaloContentInstance.class)).convert(new ByteArrayInputStream(haloContentInstance.getBytes()));
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
    private HaloContentInstance(@NonNull HaloContentInstance.Builder builder) {
        mItemId = builder.mItemId;
        mModuleName = builder.mModuleName;
        mModuleId = builder.mModuleId;
        mName = builder.mName;
        mTags = builder.mTags;
        mValues = builder.mValues;
        mAuthor = builder.mAuthor;
        mArchived = builder.mArchivedAt;
        mCreatedDate = builder.mCreatedDate;
        mLastUpdate = builder.mLastUpdate;
        mPublishedAt = builder.mPublishedAt;
        mRemovedAt = builder.mRemovedAt;
    }


    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<HaloContentInstance> {

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
         * The name of the author who created this element.
         */
        String mAuthor;

        /**
         * Creation date of this element on the Halo.
         */
        Date mCreatedDate;

        /**
         * Last updated date.
         */
        Date mLastUpdate;

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
         * Creates the builder. You can set a nullable module name.
         */
        public Builder(@Nullable String moduleName) {
            mModuleName = moduleName;
        }

        /**
         * Set the item id to use on update or delete operations.
         *
         * @param id
         * @return The builder
         */
        @NonNull
        public HaloContentInstance.Builder withId(@NonNull String id) {
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
        public HaloContentInstance.Builder withModuleId(@NonNull String moduleId) {
            AssertionUtils.notNull(moduleId, "moduleId");
            mModuleId = moduleId;
            return this;
        }

        /**
         * Set the item author.
         *
         * @param author
         * @return The builder
         */
        @NonNull
        public HaloContentInstance.Builder withAuthor(@NonNull String author) {
            AssertionUtils.notNull(author, "author");
            mAuthor = author;
            return this;
        }

        /**
         * Set the name of the content item.
         *
         * @param name
         * @return
         */
        public HaloContentInstance.Builder withName(@NonNull String name) {
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
        public HaloContentInstance.Builder withTags(@NonNull List<HaloSegmentationTag> tags) {
            AssertionUtils.notNull(tags, "tags");
            mTags = tags;
            return this;
        }

        /**
         * Set the content values.
         *
         * @param values
         * @return
         */
        public HaloContentInstance.Builder withContentData(@NonNull Object values) {
            AssertionUtils.notNull(values, "values");
            Map<String, Object> mapped = mapValues(values);
            if (mapped != null) {
                JSONObject data = new JSONObject(mapped);
                mValues = data;
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
         * Set the creation date.
         *
         * @param creationDate
         * @return
         */
        public HaloContentInstance.Builder withCreationDate(@NonNull Date creationDate) {
            AssertionUtils.notNull(creationDate, "creationDate");
            mCreatedDate = creationDate;
            return this;
        }

        /**
         * Set the last update date.
         *
         * @param lastUpdateDate
         * @return
         */
        public HaloContentInstance.Builder withLastUpdateDate(@NonNull Date lastUpdateDate) {
            AssertionUtils.notNull(lastUpdateDate, "lastUpdateDate");
            mLastUpdate = lastUpdateDate;
            return this;
        }

        /**
         * Set the publish date.
         *
         * @param publishDate
         * @return
         */
        public HaloContentInstance.Builder withPublishDate(@NonNull Date publishDate) {
            AssertionUtils.notNull(publishDate, "publishDate");
            mPublishedAt = publishDate;
            return this;
        }


        /**
         * Set the publish date.
         *
         * @param archivedDate
         * @return
         */
        public HaloContentInstance.Builder withArchivedDate(@NonNull Date archivedDate) {
            AssertionUtils.notNull(archivedDate, "archivedDate");
            mArchivedAt = archivedDate;
            return this;
        }

        /**
         * Set the removal date shceduled.
         *
         * @param removalDate
         * @return
         */
        public HaloContentInstance.Builder withRemovalDate(@NonNull Date removalDate) {
            AssertionUtils.notNull(removalDate, "removalDate");
            mRemovedAt = removalDate;
            return this;
        }

        @NonNull
        @Override
        public HaloContentInstance build() {
            return new HaloContentInstance(this);
        }
    }
}