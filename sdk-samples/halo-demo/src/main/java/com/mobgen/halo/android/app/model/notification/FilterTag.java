package com.mobgen.halo.android.app.model.notification;


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class FilterTag implements Parcelable {

    @JsonField(name = "id")
    String mId;
    @JsonField(name = "name")
    String mName;
    @JsonField(name = "value")
    String mValue;

    public FilterTag(){}

    public FilterTag(String id, String name, String value) {
        this.mId = id;
        this.mName = name;
        this.mValue = value;
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
    }

    protected FilterTag(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mValue = in.readString();
    }

    public static final Parcelable.Creator<FilterTag> CREATOR = new Parcelable.Creator<FilterTag>() {
        public FilterTag createFromParcel(Parcel source) {
            return new FilterTag(source);
        }

        public FilterTag[] newArray(int size) {
            return new FilterTag[size];
        }
    };
}

