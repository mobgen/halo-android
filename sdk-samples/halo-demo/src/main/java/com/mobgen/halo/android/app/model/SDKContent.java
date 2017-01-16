package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

@JsonObject
public class SDKContent implements Parcelable {

    @JsonField(name = "boolean")
    boolean mBoolean;

    @JsonField(name = "color")
    String mColor;

    @JsonField(name = "date")
    long mDate;

    @JsonField(name = "image")
    String mImage;

    @JsonField(name = "name")
    String mName;

    @JsonField(name = "number")
    int mNumber;

    @JsonField(name = "url")
    String mUrl;

    public SDKContent(){

    }

    public SDKContent(boolean booleano, String color, long date, String image, String name, int number, String url){
        mBoolean = booleano;
        mColor = color;
        mDate = date;
        mImage = image;
        mName = name;
        mNumber = number;
        mUrl = url;
    }

    public boolean getBoolean() {
        return mBoolean;
    }

    public long getDate() {
        return mDate;
    }

    public String getColor() {
        return mColor;
    }

    public String getImage() {
        return mImage;
    }

    public String getName() {
        return mImage;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mBoolean ? 1 : 0));
        dest.writeLong(mDate);
        dest.writeString(this.mColor);
        dest.writeString(this.mName);
        dest.writeString(this.mImage);
        dest.writeInt(this.mNumber);
        dest.writeString(this.mUrl);
    }

    protected SDKContent(Parcel in) {
        mBoolean = in.readByte() != 0;
        this.mColor = in.readString();
        this.mDate = in.readLong();
        this.mName = in.readString();
        this.mImage = in.readString();
        this.mUrl = in.readString();
        this.mNumber = in.readInt();
    }

    public static final Creator<SDKContent> CREATOR = new Creator<SDKContent>() {
        public SDKContent createFromParcel(Parcel source) {
            return new SDKContent(source);
        }

        public SDKContent[] newArray(int size) {
            return new SDKContent[size];
        }
    };
}
