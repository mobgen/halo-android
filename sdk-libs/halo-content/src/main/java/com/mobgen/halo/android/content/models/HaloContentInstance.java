package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

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
import java.util.Date;
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
            Map map = (Map) mapper.parse(jsonParser);
            return map != null ? new JSONObject(map) : null;
        }

        @Override
        public void serialize(JSONObject object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            //TODO generate serialize
        }
    }

    /**
     * The item id.
     */
    @JsonField(name = "id")
    String mItemId;

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
     * @param moduleId        The module id.
     * @param name            The name.
     * @param values          The values.
     * @param author          The author of this item.
     * @param createdDate     The creation date.
     * @param updateDate      The last update date.
     * @param publicationDate The published date.
     * @param deletionDate    The removed date.
     */
    @Keep
    @Api(2.0)
    public HaloContentInstance(String itemId, String moduleId, String name, JSONObject values, String author, Date createdDate, Date updateDate, Date publicationDate, Date deletionDate) {
        mItemId = itemId;
        mModuleId = moduleId;
        mName = name;
        mValues = values;
        mAuthor = author;
        mCreatedDate = createdDate;
        mLastUpdate = updateDate;
        mPublishedAt = publicationDate;
        mRemovedAt = deletionDate;
    }

    protected HaloContentInstance(Parcel in) {
        this.mItemId = in.readString();
        this.mModuleId = in.readString();
        this.mName = in.readString();
        try {
            String values = in.readString();
            this.mValues = values != null ? new JSONObject(values): null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The values of the general content item " + mItemId + "could not be parsed on the parceling op.");
        }
        this.mAuthor = in.readString();
        long tmpMCreatedDate = in.readLong();
        this.mCreatedDate = tmpMCreatedDate == -1 ? null : new Date(tmpMCreatedDate);
        long tmpMLastUpdate = in.readLong();
        this.mLastUpdate = tmpMLastUpdate == -1 ? null : new Date(tmpMLastUpdate);
        long tmpMPublishedAt = in.readLong();
        this.mPublishedAt = tmpMPublishedAt == -1 ? null : new Date(tmpMPublishedAt);
        long tmpMRemovedAt = in.readLong();
        this.mRemovedAt = tmpMRemovedAt == -1 ? null : new Date(tmpMRemovedAt);
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mItemId);
        dest.writeString(this.mModuleId);
        dest.writeString(this.mName);
        dest.writeString(mValues != null ? this.mValues.toString() : null);
        dest.writeString(this.mAuthor);
        dest.writeLong(mCreatedDate != null ? mCreatedDate.getTime() : -1);
        dest.writeLong(mLastUpdate != null ? mLastUpdate.getTime() : -1);
        dest.writeLong(mPublishedAt != null ? mPublishedAt.getTime() : -1);
        dest.writeLong(mRemovedAt != null ? mRemovedAt.getTime() : -1);
    }

    @Override
    public String toString() {
        return "ContentInstance{" +
                "mItemId='" + mItemId + '\'' +
                ", mModuleId='" + mModuleId + '\'' +
                ", mName='" + mName + '\'' +
                ", mValues=" + mValues +
                ", mAuthor='" + mAuthor + '\'' +
                ", mCreatedDate=" + mCreatedDate +
                ", mLastUpdate=" + mLastUpdate +
                ", mPublishedAt=" + mPublishedAt +
                ", mRemovedAt=" + mRemovedAt +
                '}';
    }
}