package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class BatchImage implements Parcelable {

    @JsonField(name = "image")
    String mImage;

    @JsonField(name = "author")
    String mAuthor;

    String mInstanceId;

    boolean isSelected = false;

    public BatchImage(){

    }

    public BatchImage(String url,String author) {
        this.mImage = url;
        this.mAuthor = author;
    }

    public String image() {
        return mImage;
    }

    public String author() {
        return mAuthor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getInstanceId(){
        return mInstanceId;
    }

    public void setInstanceId(String id){
        mInstanceId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mImage);
        dest.writeString(this.mAuthor);
    }

    protected BatchImage(Parcel in) {
        this.mImage = in.readString();
        this.mAuthor = in.readString();
    }

    public static final Creator<BatchImage> CREATOR = new Creator<BatchImage>() {
        public BatchImage createFromParcel(Parcel source) {
            return new BatchImage(source);
        }

        public BatchImage[] newArray(int size) {
            return new BatchImage[size];
        }
    };
}
