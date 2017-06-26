package com.mobgen.halo.android.app.model;

/**
 * Created by f.souto.gonzalez on 26/06/2017.
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

@JsonObject
public class UserData implements Parcelable {

    @JsonField(name = "Name")
    String mName;

    @JsonField(name = "Date")
    Date mDate;

    @JsonField(name = "Thumbnail")
    String mThumbnail;

    public UserData() {

    }

    public UserData(String name, Date date, String thumbnail) {
        mDate = date;
        mName = name;
        mThumbnail = thumbnail;
    }

    public String getUserName() {
        return mName;
    }

    public Date getDate() {
        return mDate;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeLong(mDate != null ? mDate.getTime() : -1);
        dest.writeString(this.mThumbnail);
    }

    protected UserData(Parcel in) {
        this.mName = in.readString();
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mThumbnail = in.readString();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        public UserData createFromParcel(Parcel source) {
            return new UserData(source);
        }

        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
}
