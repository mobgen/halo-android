package com.mobgen.halo.android.app.model.notification;


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.app.model.chat.ChatMessage;
import com.mobgen.halo.android.app.model.chat.QRContact;

@JsonObject
public class NotificationData implements Parcelable {

    @JsonField(name = "content_available")
    int mContentAvailable;
    @JsonField(name = "custom")
    QRContact mQRContact;
    @JsonField(name = "custom")
    ChatMessage mChatMessage;

    public NotificationData() {
    }

    public NotificationData(ChatMessage custom) {
        mContentAvailable = 0;
        mChatMessage = custom;
    }

    public NotificationData(QRContact custom) {
        mContentAvailable = 0;
        mQRContact = custom;
    }

    public NotificationData(boolean contentAvailable,ChatMessage custom) {
        mContentAvailable = contentAvailable ? 1 : 0;
        mContentAvailable = 0;
        mChatMessage = custom;
    }

    public NotificationData(boolean contentAvailable,QRContact custom) {
        mContentAvailable = contentAvailable ? 1 : 0;
        mContentAvailable = 0;
        mQRContact = custom;
    }

    public boolean isSilent() {
        return mContentAvailable == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mContentAvailable);
    }

    protected NotificationData(Parcel in) {
        this.mContentAvailable = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<NotificationData> CREATOR = new Parcelable.Creator<NotificationData>() {
        public NotificationData createFromParcel(Parcel source) {
            return new NotificationData(source);
        }

        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };
}

