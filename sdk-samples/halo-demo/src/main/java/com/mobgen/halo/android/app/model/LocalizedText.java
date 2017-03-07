package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by fernando souto on 06/02/17.
 */

@JsonObject
public class LocalizedText implements Parcelable {


    @JsonField(name = "en-GB")
    public String mEnGB;

    @JsonField(name = "es-ES")
    public String mEsEs;

    @JsonField(name = "zh-CN")
    public String mZhCn;

    @JsonField(name = "nl-NL")
    public String mNlNl;

    public LocalizedText() {
    }

    public LocalizedText(String enGB, String esEs, String zhCn, String nlNl) {
        this.mEnGB = enGB;
        this.mEsEs = esEs;
        this.mZhCn = zhCn;
        this.mNlNl = nlNl;
    }

    protected LocalizedText(Parcel in) {
        mEnGB = in.readString();
        mEsEs = in.readString();
        mZhCn = in.readString();
        mNlNl = in.readString();
    }

    public static final Creator<LocalizedText> CREATOR = new Creator<LocalizedText>() {
        @Override
        public LocalizedText createFromParcel(Parcel in) {
            return new LocalizedText(in);
        }

        @Override
        public LocalizedText[] newArray(int size) {
            return new LocalizedText[size];
        }
    };

    public String getEnGB() {
        return mEnGB;
    }

    public void setEnGB(String enGB) {
        this.mEnGB = enGB;
    }

    public String getEsEs() {
        return mEsEs;
    }

    public void setEsEs(String esEs) {
        this.mEsEs = esEs;
    }

    public String getZhCn() {
        return mZhCn;
    }

    public void setZhCn(String zhCn) {
        this.mZhCn = zhCn;
    }

    public String getNlNl() {
        return mNlNl;
    }

    public void setNlNl(String nlNl) {
        this.mNlNl = nlNl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEnGB);
        dest.writeString(mEsEs);
        dest.writeString(mZhCn);
        dest.writeString(mNlNl);
    }
}
