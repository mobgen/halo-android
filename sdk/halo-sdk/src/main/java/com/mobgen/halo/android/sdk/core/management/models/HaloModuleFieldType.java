package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.Date;
import java.util.List;

/**
 * The HALO module field type define the data type of each element.
 */
@Keep
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

    public HaloModuleFieldType(String mName, List<HaloModuleFieldRule> mRules, Boolean mIsLongvalue, Date mDeleteDate, Date mLastUpdate, Date mCreationDate, String mId) {
        this.mName = mName;
        this.mRules = mRules;
        this.mIsLongvalue = mIsLongvalue;
        this.mDeleteDate = mDeleteDate;
        this.mLastUpdate = mLastUpdate;
        this.mCreationDate = mCreationDate;
        this.mId = mId;
    }

    /**
     * Parsing empty constructor.
     */
    public HaloModuleFieldType() {
        //Empty constructor for parsing
    }

    /**
     * Provides the name of the field type.
     * @return The field type name.
     */
    @NonNull
    @Api(2.3)
    public String getName() {
        return mName;
    }

    /**
     * Provides the validation rules for a module field type.
     * @return The validation rules of the field type.
     */
    @NonNull
    @Api(2.3)
    public List<HaloModuleFieldRule> getRules() {
        return mRules;
    }

    /**
     * Provides long value.
     * @return True if its a long value. Otherwise false.
     */
    @NonNull
    @Api(2.3)
    public Boolean getIsLongvalue() {
        return mIsLongvalue;
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

    protected HaloModuleFieldType(Parcel in) {
        this.mName = in.readString();
        this.mId = in.readString();
        this.mIsLongvalue = in.readByte() != 0;
        this.mRules = in.createTypedArrayList(HaloModuleFieldRule.CREATOR);
        long tmpMCreatedDate = in.readLong();
        this.mCreationDate = tmpMCreatedDate == -1 ? null : new Date(tmpMCreatedDate);
        long tmpMLastUpdate = in.readLong();
        this.mLastUpdate = tmpMLastUpdate == -1 ? null : new Date(tmpMLastUpdate);
        long tmpMRemovedAt = in.readLong();
        this.mDeleteDate = tmpMRemovedAt == -1 ? null : new Date(tmpMRemovedAt);
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
        dest.writeByte((byte) (mIsLongvalue ? 1 : 0));
        dest.writeTypedList(mRules);
        dest.writeLong(mCreationDate != null ? mCreationDate.getTime() : -1);
        dest.writeLong(mLastUpdate != null ? mLastUpdate.getTime() : -1);
        dest.writeLong(mDeleteDate != null ? mDeleteDate.getTime() : -1);
    }
}
