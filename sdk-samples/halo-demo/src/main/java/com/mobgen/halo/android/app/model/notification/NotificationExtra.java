package com.mobgen.halo.android.app.model.notification;


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.app.model.chat.ChatMessage;

@JsonObject
public class NotificationExtra implements Parcelable {

    @JsonField(name = "custom")
    ChatMessage mCustom;

    public NotificationExtra() {
    }

    public NotificationExtra(ChatMessage custom) {
        mCustom = custom;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    protected NotificationExtra(Parcel in) {

    }

    public static final Creator<NotificationExtra> CREATOR = new Creator<NotificationExtra>() {
        public NotificationExtra createFromParcel(Parcel source) {
            return new NotificationExtra(source);
        }

        public NotificationExtra[] newArray(int size) {
            return new NotificationExtra[size];
        }
    };
}

