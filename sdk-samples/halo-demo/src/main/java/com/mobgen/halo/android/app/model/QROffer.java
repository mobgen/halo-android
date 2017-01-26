package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;

import java.util.Date;

@JsonObject
@HaloSearchable(version = 15)
public class QROffer implements Parcelable {

    private String mId;

    @JsonField(name = "Title")
    String mTitle;

    @JsonField(name = "Date")
    Date mDate;

    @JsonField(name = "ContentHtml")
    String mArticle;

    @JsonField(name = "QRImage")
    String mQRImage;

    @JsonField(name = "Thumbnail")
    String mThumbnail;

    public QROffer(){

    }

    public QROffer(String id, String title, Date date, String article, String thumbnail) {
        mId = id;
        mTitle = title;
        mDate = date;
        mArticle = article;
        mThumbnail = thumbnail;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }
    @HaloQuery(name="selectDate",query="select * from HALO_GC_QROFFER where GC_MDATE = ?mDate:Date orderBy id DESCENDANT")
    public Date getDate() {
        return mDate;
    }

    public String getArticle() {
        return mArticle;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getQRImage() {
        return mQRImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeLong(mDate != null ? mDate.getTime() : -1);
        dest.writeString(this.mArticle);
        dest.writeString(this.mThumbnail);
        dest.writeString(this.mQRImage);
    }

    protected QROffer(Parcel in) {
        this.mId = in.readString();
        this.mTitle = in.readString();
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mArticle = in.readString();
        this.mThumbnail = in.readString();
        this.mQRImage = in.readString();
    }

    public static final Creator<QROffer> CREATOR = new Creator<QROffer>() {
        public QROffer createFromParcel(Parcel source) {
            return new QROffer(source);
        }

        public QROffer[] newArray(int size) {
            return new QROffer[size];
        }
    };
}
