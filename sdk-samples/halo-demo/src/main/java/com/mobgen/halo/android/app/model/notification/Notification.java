package com.mobgen.halo.android.app.model.notification;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;

@JsonObject
public class Notification implements Parcelable {

    @JsonField(name = "body")
    String mBody;
    @JsonField(name = "title")
    String mTitle;
    @JsonField(name = "badge")
    String mBadge;
    @JsonField(name = "color")
    String mColor;
    @JsonField(name = "sound")
    String mSound;

    public Notification(){}

    private Notification(Builder builder) {
        mTitle = builder.mTitle;
        mBody = builder.mBody;
        mBadge = builder.mBadge;
        mColor = builder.mColor;
        mSound = builder.mSound;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBody() {
        return mBody;
    }

    public String getTitle() {
        return mTitle;
    }

    public Integer getBadge() {
        if (mBadge != null) {
            return Integer.valueOf(mBadge);
        }
        return null;
    }

    public String getColor() {
        return mColor;
    }

    public String getSound() {
        return mSound;
    }

    public static class Builder implements IBuilder<Notification> {
        private String mBody;
        private String mTitle;
        private String mBadge;
        private String mColor;
        private String mSound;

        public Builder setBody(String body) {
            this.mBody = body;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setBadge(int badge) {
            this.mBadge = String.valueOf(badge);
            return this;
        }

        public Builder setColour(String color) {
            this.mColor = color;
            return this;
        }

        public Builder setSound(String sound) {
            this.mSound = sound;
            return this;
        }

        @NonNull
        @Override
        public Notification build() {
            return new Notification(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBody);
        dest.writeString(this.mTitle);
        dest.writeString(this.mBadge);
        dest.writeString(this.mColor);
        dest.writeString(this.mSound);
    }

    protected Notification(Parcel in) {
        this.mBody = in.readString();
        this.mTitle = in.readString();
        this.mBadge = in.readString();
        this.mColor = in.readString();
        this.mSound = in.readString();
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}