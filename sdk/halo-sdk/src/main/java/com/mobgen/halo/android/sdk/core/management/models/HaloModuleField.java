package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * The halo module field are the subcomponent that create the structure of a halo module.
 */
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
    @JsonField
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

    protected HaloModuleField(Parcel in) {
        mModuleFieldType = in.readParcelable(HaloModuleFieldType.class.getClassLoader());
        mModule = in.readString();
        mName = in.readString();
        mFormat = in.readString();
        mDescription = in.readString();
        mCustomerId = in.readInt();
        mId = in.readString();
    }

    public HaloModuleFieldType getModuleFieldType() {
        return mModuleFieldType;
    }

    public String getModule() {
        return mModule;
    }

    public String getName() {
        return mName;
    }

    public String getFormat() {
        return mFormat;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getCustomerId() {
        return mCustomerId;
    }

    public Date getDeleteDate() {
        return mDeleteDate;
    }

    public Date getLastUpdate() {
        return mLastUpdate;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mModuleFieldType, flags);
        dest.writeString(mModule);
        dest.writeString(mName);
        dest.writeString(mFormat);
        dest.writeString(mDescription);
        dest.writeInt(mCustomerId);
        dest.writeString(mId);
    }

    /**
     * Provides the serializer given the factory.
     *
     * @param haloModuleField The object to serialize.
     * @param parser   The parser factory.
     * @return The parser obtained.
     */
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
