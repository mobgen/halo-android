package com.mobgen.halo.android.app.model.notification;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompatExtras;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.app.model.chat.ChatMessage;
import com.mobgen.halo.android.app.model.chat.QRContact;


@JsonObject
public class Payload implements Parcelable {

    @JsonField(name = "notification")
    Notification mNotification;
    @JsonField(name = "data")
    NotificationData mData;

    public Payload(){}

    public Payload(Notification notification, boolean silent, ChatMessage chatMessage) {
        mNotification = notification;
        mData = new NotificationData(silent,chatMessage);
    }

    public Payload(Notification notification, boolean silent, QRContact qrContact) {
        mNotification = notification;
        mData = new NotificationData(silent,qrContact);
    }

    public Notification getNotification() {
        return mNotification;
    }

    public NotificationData getData() {
        return mData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mNotification, flags);
        dest.writeParcelable(this.mData, flags);
    }

    protected Payload(Parcel in) {
        this.mNotification = in.readParcelable(Notification.class.getClassLoader());
        this.mData = in.readParcelable(NotificationData.class.getClassLoader());
    }

    public static final Parcelable.Creator<Payload> CREATOR = new Parcelable.Creator<Payload>() {
        public Payload createFromParcel(Parcel source) {
            return new Payload(source);
        }

        public Payload[] newArray(int size) {
            return new Payload[size];
        }
    };
}