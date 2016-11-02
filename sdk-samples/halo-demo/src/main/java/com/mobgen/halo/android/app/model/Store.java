package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Store locator model.
 */
@JsonObject
public class Store implements Parcelable {

    @JsonField(name = "Name")
    String mName;

    @JsonField(name = "Latitude")
    Double mLatitude;

    @JsonField(name = "Longitude")
    Double mLongitude;

    public Store(){

    }

    public Store(@NonNull String name, @NonNull Double latitude, @NonNull Double longitude) {
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getName() {
        return mName;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeValue(this.mLatitude);
        dest.writeValue(this.mLongitude);
    }

    private Store(Parcel in) {
        this.mName = in.readString();
        this.mLatitude = (Double) in.readValue(Long.class.getClassLoader());
        this.mLongitude = (Double) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<Store> CREATOR = new Parcelable.Creator<Store>() {
        public Store createFromParcel(Parcel source) {
            return new Store(source);
        }

        public Store[] newArray(int size) {
            return new Store[size];
        }
    };
}
