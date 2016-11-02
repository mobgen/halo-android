package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class GalleryImage implements Parcelable {

    @JsonField(name = "url")
    String mUrl;

    public GalleryImage(){

    }

    public GalleryImage(String url) {
        this.mUrl = url;
    }

    public String url() {
        return mUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUrl);
    }

    protected GalleryImage(Parcel in) {
        this.mUrl = in.readString();
    }

    public static final Parcelable.Creator<GalleryImage> CREATOR = new Parcelable.Creator<GalleryImage>() {
        public GalleryImage createFromParcel(Parcel source) {
            return new GalleryImage(source);
        }

        public GalleryImage[] newArray(int size) {
            return new GalleryImage[size];
        }
    };
}
