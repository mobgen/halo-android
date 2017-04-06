package com.mobgen.halo.android.app.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

@JsonObject
public class Schedule implements Parcelable {

    @JsonField(name = "name")
    String mName;
    @JsonField(name = "appId")
    Integer mAppId;
    @JsonField(name = "aliases")
    String[] mAlias;
    @JsonField(name = "filterTags")
    FilterTag[] mFilterTags;
    @JsonField(name = "payload")
    Payload mPayload;
    @JsonField(name = "sendOn")
    Date mSendOn;
    private transient boolean mShouldArrive;

    public Schedule(){}

    public Schedule(String name, Integer appId, String[] alias, FilterTag[] filterTags, Payload payload, boolean shouldArrive) {
        mName = name;
        mAppId = appId;
        mPayload = payload;
        mSendOn = new Date();
        mAlias = alias;
        mFilterTags = filterTags;
        mShouldArrive = shouldArrive;
    }

    public String getName() {
        return mName;
    }

    public Integer getAppId() {
        return mAppId;
    }

    public Payload getPayload() {
        return mPayload;
    }

    public Date getSendOn() {
        return mSendOn;
    }

    public String[] getAliases() {
        return mAlias;
    }

    public boolean shouldArrive() {
        return mShouldArrive;
    }

    public String getContentDescription() {
        StringBuilder builder = new StringBuilder();
        if (mPayload.getNotification().getTitle() != null) {
            builder.append("Title ");
        }
        if (mPayload.getNotification().getBadge() != null) {
            builder.append("Badge ");
        }
        if (mPayload.getNotification().getBody() != null) {
            builder.append("Body ");
        }
        if (mPayload.getNotification().getColor() != null) {
            builder.append("Color ");
        }
        if (mPayload.getNotification().getSound() != null) {
            builder.append("Sound ");
        }
        if (mShouldArrive) {
            builder.append("[Should arrive] ");
        } else {
            builder.append("[Should NOT arrive] ");
        }
        if (mAlias != null) {
            builder.append("With alias ");
        }
        if (mFilterTags != null) {
            builder.append("With filter tags ");
        }
        if (mPayload.getData().isSilent()) {
            builder.append("Silent ");
        } else {
            builder.append("NotSilent ");
        }
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeValue(this.mAppId);
        dest.writeStringArray(this.mAlias);
        dest.writeParcelableArray(this.mFilterTags, 0);
        dest.writeParcelable(this.mPayload, 0);
        dest.writeLong(mSendOn != null ? mSendOn.getTime() : -1);
        dest.writeByte(mShouldArrive ? (byte) 1 : (byte) 0);
    }

    protected Schedule(Parcel in) {
        this.mName = in.readString();
        this.mAppId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mAlias = in.createStringArray();
        this.mFilterTags = (FilterTag[]) in.readParcelableArray(FilterTag.class.getClassLoader());
        this.mPayload = in.readParcelable(Payload.class.getClassLoader());
        long tmpMSendOn = in.readLong();
        this.mSendOn = tmpMSendOn == -1 ? null : new Date(tmpMSendOn);
        this.mShouldArrive = in.readByte() != 0;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }

        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}