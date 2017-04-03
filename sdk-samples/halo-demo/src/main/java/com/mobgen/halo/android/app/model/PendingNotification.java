package com.mobgen.halo.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloField;
import com.mobgen.halo.android.content.annotations.HaloQueries;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;

/**
 * Created by fernandosouto on 30/03/17.
 */
@JsonObject
@HaloSearchable(version = 7 , tableName ="PendingNotification")
@HaloQueries(queries = {@HaloQuery(name="getPendingMessages", query="select * from PendingNotification where NotificationId = @{mNotificationID:Integer}"),
        @HaloQuery(name="savePendingMessage", query="insert into PendingNotification(NotificationId,Message) VALUES (@{mNotificationID:Integer},@{mMessage:String})"),
        @HaloQuery(name="deletePendingMessages", query="delete from PendingNotification where NotificationId = @{mNotificationID:Integer}")})
public class PendingNotification implements Parcelable {

    @JsonField(name = "NotificationId")
    Integer mNotificationId;

    @JsonField(name = "Message")
    String mMessage;

    @HaloConstructor( columnNames = {"NotificationId","Message"})
    public PendingNotification(@NonNull Integer notificationId, @NonNull String message){
        mNotificationId = notificationId;
        mMessage = message;
    }

    public PendingNotification(){

    }

    protected PendingNotification(Parcel in) {
        mNotificationId = in.readInt();
        mMessage = in.readString();
    }

    public static final Creator<PendingNotification> CREATOR = new Creator<PendingNotification>() {
        @Override
        public PendingNotification createFromParcel(Parcel in) {
            return new PendingNotification(in);
        }

        @Override
        public PendingNotification[] newArray(int size) {
            return new PendingNotification[size];
        }
    };

    public String getMessage() {
        return mMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNotificationId);
        dest.writeString(mMessage);
    }
}


