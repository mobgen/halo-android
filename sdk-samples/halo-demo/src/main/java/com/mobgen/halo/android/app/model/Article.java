package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloField;
import com.mobgen.halo.android.content.annotations.HaloQueries;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;

import java.util.Date;

/**
 * Created by javierdepedrolopez on 9/11/15.
 */
@JsonObject
@HaloSearchable(version = 13 , tableName = "Article")
@HaloQueries(queries = {@HaloQuery(name="deleteByTitle", query=("delete from Article where Title = @{mTitle:String}")),
        @HaloQuery(name="selectTitle",query="select * from Article where Title = @{mTitle:String}"),
        @HaloQuery(name="insertArticle",query="insert into Article(Title,Date,ContentHtml,Summary,Thumbnail,Image) VALUES (@{mTitle:String},@{mDate:Date},@{mArticle:String},@{mSummary:String},@{mThumbnail:String},@{mImage:String});")
})
public class Article implements Parcelable {
    @HaloField(index = true,columnName = "Title")
    @JsonField(name = "Title")
    String mTitle;

    @JsonField(name = "Date")
    Date mDate;

    @JsonField(name = "ContentHtml")
    String mArticle;

    @JsonField(name = "Summary")
    String mSummary;

    @JsonField(name = "Thumbnail")
    String mThumbnail;

    @HaloField(index = true,columnName = "Image")
    @JsonField(name = "Image")
    String mImage;

    public Article(){

    }

    @HaloConstructor( columnNames = {"Title","Date","ContentHtml","Summary","Thumbnail","Image"})
    public Article(String title, Date date, String article, String summary, String thumnail, String image) {
        mTitle = title;
        mDate = date;
        mArticle = article;
        mSummary = summary;
        mThumbnail = thumnail;
        mImage = image;
    }


    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public String getArticle() {
        return mArticle;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getImage() {
        return mImage;
    }

    public String getSummary() {
        return mSummary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeLong(mDate != null ? mDate.getTime() : -1);
        dest.writeString(this.mArticle);
        dest.writeString(this.mThumbnail);
        dest.writeString(this.mSummary);
        dest.writeString(this.mImage);
    }

    protected Article(Parcel in) {
        this.mTitle = in.readString();
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mArticle = in.readString();
        this.mThumbnail = in.readString();
        this.mSummary = in.readString();
        this.mImage = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
