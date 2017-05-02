package com.mobgen.halo.android.app.model.notification;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

@JsonObject
public class NotificationRequest implements Parcelable {

    @JsonField(name = "id")
    String mRequestId;
    private transient Schedule mSchedule;
    private Date mRequestTime;

    public NotificationRequest() {
        mRequestTime = new Date();
    }

    public NotificationRequest(String requestId) {
        this();
        mRequestId = requestId;
    }

    @Nullable
    public String getRequestId() {
        return mRequestId;
    }

    public Schedule getSchedule() {
        return mSchedule;
    }

    public void setSchedule(Schedule schedule) {
        mSchedule = schedule;
    }

    public Date getRequestTime() {
        return mRequestTime;
    }

    public boolean shouldArrive() {
        return mSchedule == null || mSchedule.shouldArrive();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mRequestId);
        dest.writeParcelable(this.mSchedule, 0);
        dest.writeLong(mRequestTime != null ? mRequestTime.getTime() : -1);
    }

    protected NotificationRequest(Parcel in) {
        this.mRequestId = in.readString();
        this.mSchedule = in.readParcelable(Schedule.class.getClassLoader());
        long tmpMRequestTime = in.readLong();
        this.mRequestTime = tmpMRequestTime == -1 ? null : new Date(tmpMRequestTime);
    }

    public static final Creator<NotificationRequest> CREATOR = new Creator<NotificationRequest>() {
        public NotificationRequest createFromParcel(Parcel source) {
            return new NotificationRequest(source);
        }

        public NotificationRequest[] newArray(int size) {
            return new NotificationRequest[size];
        }
    };
}

