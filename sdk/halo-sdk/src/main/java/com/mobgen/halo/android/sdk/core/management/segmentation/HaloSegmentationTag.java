package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.Serializable;

/**
 * Model that refers to segmentation tags.
 */
@Keep
@JsonObject
public class HaloSegmentationTag implements Comparable<HaloSegmentationTag>, Parcelable {

    /**
     * The id of the segmentation tag.
     */
    @JsonField(name = "id")
    String mId;

    /**
     * Push notification tag.
     */
    @JsonField(name = "name")
    String mName;

    /**
     * The value of this tag. This is not a mandatory field.
     */
    @JsonField(name = "value")
    String mValue;

    /**
     * The tag type.
     */
    @JsonField(name = "tagType")
    String mTagType;

    public static final Parcelable.Creator<HaloSegmentationTag> CREATOR = new Parcelable.Creator<HaloSegmentationTag>() {
        public HaloSegmentationTag createFromParcel(Parcel source) {
            return new HaloSegmentationTag(source);
        }

        public HaloSegmentationTag[] newArray(int size) {
            return new HaloSegmentationTag[size];
        }
    };

    /**
     * Parsing empty constructor.
     */
    protected HaloSegmentationTag() {
        //Empty constructor for parsing
    }

    /**
     * Constructor for segmentation tags.
     *
     * @param name  The name of the segmentation tag.
     * @param value The value of the segmentation tag.
     */
    @Api(1.0)
    public HaloSegmentationTag(String name, Serializable value) {
        this(name, value, false);
    }

    /**
     * Constructor for the parcelable interface.
     *
     * @param in Parcel.
     */
    protected HaloSegmentationTag(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mValue = in.readString();
        this.mTagType = in.readString();
    }

    /**
     * Constructor for the system tags.
     *
     * @param name     The name of the tag.
     * @param value    The value of the tag.
     * @param isDevice True if it is a system tag.
     */
    private HaloSegmentationTag(String name, Serializable value, boolean isDevice) {
        mTagType = isDevice ? "000000000000000000000001" : "000000000000000000000002";
        mName = name;
        if (value != null) {
            mValue = value.toString();
        }
    }

    /**
     * Creates a system tag.
     *
     * @param name  The name for the tag.
     * @param value The value for the tag.
     * @return The segmentation tag.
     */
    @NonNull
    protected static HaloSegmentationTag createDeviceTag(String name, Serializable value) {
        return new HaloSegmentationTag(name, value, true);
    }

    /**
     * Provides the segmentation tag.
     *
     * @return The name of the tag.
     */
    @Api(1.0)
    public String getName() {
        return mName;
    }

    /**
     * Provides the id of the segmentation tag.
     *
     * @return The id.
     */
    @Api(1.0)
    @Nullable
    public String getId() {
        return mId;
    }

    /**
     * The value of this tag if it is required.
     *
     * @return The value of this tag.
     */
    @Api(1.0)
    public String getValue() {
        return mValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HaloSegmentationTag) {
            HaloSegmentationTag otherTag = (HaloSegmentationTag) o;
            return otherTag.mName.equals(getName());
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tag ").append(String.format("%1$-" + 30 + "s", mName));
        if (mValue != null) {
            builder.append(" | ").append(mValue);
        }
        return builder.toString();
    }

    @Override
    public int compareTo(@NonNull HaloSegmentationTag another) {
        return mName.compareToIgnoreCase(another.mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mValue);
        dest.writeString(this.mTagType);
    }
}
