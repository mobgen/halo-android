package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;
import java.util.List;

/**
 * Created by mobgenimac on 8/2/17.
 */
@JsonObject
public class HaloModuleFieldType implements Parcelable{


    /**
     * The field type name.
     */
    @JsonField(name = "name")
    String mName;

    /**
     *  The field type rules.
     */
    @JsonField(name = "rules")
    List<HaloModuleFieldRule> mRules;

    /**
     * If its a long value.
     */
    @JsonField(name = "isLongValue")
    Boolean mIsLongvalue;

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
     * The field type id.
     */
    @JsonField(name = "id")
    String mId;

    /**
     * Parsing empty constructor.
     */
    public HaloModuleFieldType() {
        //Empty constructor for parsing
    }

    protected HaloModuleFieldType(Parcel in) {
        mName = in.readString();
        mId = in.readString();
    }

    public static final Creator<HaloModuleFieldType> CREATOR = new Creator<HaloModuleFieldType>() {
        @Override
        public HaloModuleFieldType createFromParcel(Parcel in) {
            return new HaloModuleFieldType(in);
        }

        @Override
        public HaloModuleFieldType[] newArray(int size) {
            return new HaloModuleFieldType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mId);
    }
}
