package com.mobgen.locationpoc.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

/**
 * Created by f.souto.gonzalez on 08/06/2017.
 */
@JsonObject
public class Friend implements Parcelable {

    @JsonField(name = "name")
    String userName;
    @JsonField(name = "mail")
    String userMail;
    @JsonField(name = "photo")
    String userPhoto;
    @JsonField(name = "latitude")
    double latitude;
    @JsonField(name = "longitude")
    double longitude;
    @JsonField(name = "room")
    String room;
    @JsonField(name = "time")
    Date time;

    public Friend(){
        //empty constructor
    }

    protected Friend(Parcel in) {
        userName = in.readString();
        userMail = in.readString();
        userPhoto = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        room = in.readString();
        time = new Date(in.readLong());
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userMail);
        dest.writeString(userPhoto);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(room);
        dest.writeLong(time.getTime());
    }
}
