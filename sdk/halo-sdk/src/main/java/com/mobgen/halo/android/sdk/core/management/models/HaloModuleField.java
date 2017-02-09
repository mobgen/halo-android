package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * The halo module field are the subcomponents that create the meta data structure of a halo module.
 */
@Keep
@JsonObject
public class HaloModuleField implements Parcelable {

    /**
     *  The field type.
     */
    @JsonField(name="fieldType")
    HaloModuleFieldType mModuleFieldType;

    /**
     * The module id
     */
    @JsonField(name="module")
    String mModule;

    /**
     * The field name.
     */
    @JsonField(name="name")
    String mName;

    /**
     *  The field format
     */
    @JsonField(name="format")
    String mFormat;

    /**
     * The field description.
     */
    @JsonField(name="description")
    String mDescription;

    /**
     * The customer id
     */
    @JsonField(name = "customerId")
    int mCustomerId;

    /**
     * Date with the last moment this middleware was deleted.
     */
    @JsonField(name = "deletedAt")
    Date mDeleteDate;

    /**
     * Date with the last moment this middleware was updated.
     */
    @JsonField(name = "updatedAt")
    Date mLastUpdate;
    /**
     * Date that tells you when this middleware was created.
     */
    @JsonField(name = "createdAt")
    Date mCreationDate;

    /**
     * Field id
     */
    @JsonField(name="id")
    String mId;

    /**
     * Parsing empty constructor.
     */
    public HaloModuleField() {
        //Empty constructor for parsing
    }

    public HaloModuleField(HaloModuleFieldType mModuleFieldType, String mModule, String mName, String mFormat, String mDescription, int mCustomerId, Date mDeleteDate, Date mLastUpdate, Date mCreationDate, String mId) {
        this.mModuleFieldType = mModuleFieldType;
        this.mModule = mModule;
        this.mName = mName;
        this.mFormat = mFormat;
        this.mDescription = mDescription;
        this.mCustomerId = mCustomerId;
        this.mDeleteDate = mDeleteDate;
        this.mLastUpdate = mLastUpdate;
        this.mCreationDate = mCreationDate;
        this.mId = mId;
    }

    /**
     * Provides the Halo module field type.
     * @return The field type.
     */

    @NonNull
    @Api(2.3)
    public HaloModuleFieldType getModuleFieldType() {
        return mModuleFieldType;
    }

    /**
     * Provides the Halo module name id
     * @return The module id.
     */
    @NonNull
    @Api(2.3)
    public String getModule() {
        return mModule;
    }

    /**
     * Provides the field name.
     * @return The field name.
     */
    @NonNull
    @Api(2.3)
    public String getName() {
        return mName;
    }

    /**
     * Provides the module format.
     * @return The field format.
     */
    @NonNull
    @Api(2.3)
    public String getFormat() {
        return mFormat;
    }

    /**
     * Provides the field description.
     * @return The field description.
     */
    @NonNull
    @Api(2.3)
    public String getDescription() {
        return mDescription;
    }

    /**
     * Provides the customer id.
     * @return The customer id.
     */
    @NonNull
    @Api(2.3)
    public int getCustomerId() {
        return mCustomerId;
    }

    /**
     * Provides the field date delete date.
     * @return The delete date.
     */
    @NonNull
    @Api(2.3)
    public Date getDeleteDate() {
        return mDeleteDate;
    }

    /**
     * Provides the last update date.
     * @return The las update date.
     */
    @NonNull
    @Api(2.3)
    public Date getLastUpdate() {
        return mLastUpdate;
    }

    /**
     * Provides the creation date of the field.
     * @return The field craetion date.
     */
    @NonNull
    @Api(2.3)
    public Date getCreationDate() {
        return mCreationDate;
    }

    /**
     * Provides the id of the field.
     * @return The field id.
     */
    @NonNull
    public String getId() {
        return mId;
    }

    public static final Creator<HaloModuleField> CREATOR = new Creator<HaloModuleField>() {
        @Override
        public HaloModuleField createFromParcel(Parcel in) {
            return new HaloModuleField(in);
        }

        @Override
        public HaloModuleField[] newArray(int size) {
            return new HaloModuleField[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected HaloModuleField(Parcel in) {
        mModuleFieldType = in.readParcelable(HaloModuleFieldType.class.getClassLoader());
        mModule = in.readString();
        mName = in.readString();
        mFormat = in.readString();
        mDescription = in.readString();
        mCustomerId = in.readInt();
        mId = in.readString();
        long tmpMCreatedDate = in.readLong();
        this.mCreationDate = tmpMCreatedDate == -1 ? null : new Date(tmpMCreatedDate);
        long tmpMLastUpdate = in.readLong();
        this.mLastUpdate = tmpMLastUpdate == -1 ? null : new Date(tmpMLastUpdate);
        long tmpMRemovedAt = in.readLong();
        this.mDeleteDate = tmpMRemovedAt == -1 ? null : new Date(tmpMRemovedAt);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mModuleFieldType, flags);
        dest.writeString(mModule);
        dest.writeString(mName);
        dest.writeString(mFormat);
        dest.writeString(mDescription);
        dest.writeInt(mCustomerId);
        dest.writeString(mId);
        dest.writeLong(mCreationDate != null ? mCreationDate.getTime() : -1);
        dest.writeLong(mLastUpdate != null ? mLastUpdate.getTime() : -1);
        dest.writeLong(mDeleteDate != null ? mDeleteDate.getTime() : -1);
    }

    /**
     * Provides the serializer given the factory.
     *
     * @param haloModuleField The object to serialize.
     * @param parser   The parser factory.
     * @return The parser obtained.
     */
    @Api(2.3)
    public static String serialize(@NonNull HaloModuleField haloModuleField, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloModuleField, "haloContentInstance");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<HaloModuleField, String>) parser.serialize(HaloModuleField.class)).convert(haloModuleField);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the HaloContentInstance", e);
        }
    }

    /**
     * Parses a Halo content instance.
     *
     * @param haloModuleField   The haloModuleField as string.
     * @param parser The parser.
     * @return The haloModuleField parsed or an empty haloModuleField if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    @Api(2.3)
    public static HaloModuleField deserialize(@Nullable String haloModuleField, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (haloModuleField != null) {
            try {
                return ((Parser<InputStream, HaloModuleField>) parser.deserialize(HaloModuleField.class)).convert(new ByteArrayInputStream(haloModuleField.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the halocontentInstance", e);
            }
        }
        return null;
    }
}
