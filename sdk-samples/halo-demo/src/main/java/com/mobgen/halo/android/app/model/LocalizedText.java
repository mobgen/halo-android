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
    public String enGB;

    @JsonField(name = "es-ES")
    public String esEs;

    @JsonField(name = "zh-CN")
    public String zhCn;

    @JsonField(name = "nl-NL")
    public String nlNl;

    public LocalizedText() {
    }

    public LocalizedText(String enGB, String esEs, String zhCn, String nlNl) {
        this.enGB = enGB;
        this.esEs = esEs;
        this.zhCn = zhCn;
        this.nlNl = nlNl;
    }

    protected LocalizedText(Parcel in) {
        enGB = in.readString();
        esEs = in.readString();
        zhCn = in.readString();
        nlNl = in.readString();
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
        return enGB;
    }

    public void setEnGB(String enGB) {
        this.enGB = enGB;
    }

    public String getEsEs() {
        return esEs;
    }

    public void setEsEs(String esEs) {
        this.esEs = esEs;
    }

    public String getZhCn() {
        return zhCn;
    }

    public void setZhCn(String zhCn) {
        this.zhCn = zhCn;
    }

    public String getNlNl() {
        return nlNl;
    }

    public void setNlNl(String nlNl) {
        this.nlNl = nlNl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(enGB);
        dest.writeString(esEs);
        dest.writeString(zhCn);
        dest.writeString(nlNl);
    }
}
