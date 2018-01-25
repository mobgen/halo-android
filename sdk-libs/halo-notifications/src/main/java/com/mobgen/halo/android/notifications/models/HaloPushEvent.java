package com.mobgen.halo.android.notifications.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;

/**
 * Created by f.souto.gonzalez on 19/01/2018.
 */

/**
 * HALO push event action notification.
 */
@Keep
@JsonObject
public class HaloPushEvent implements Parcelable {

    /**
     * The device alias
     */
    @JsonField(name = "device")
    String device;

    /**
     * The schedule id of the push notification
     */
    @JsonField(name = "schedule")
    String schedule;

    /**
     * The event type
     */
    @JsonField(name = "action")
    @NotificationEventsActions.EventType
    String action;

    /**
     * The platform
     */
    @JsonField(name = "platform")
    String platform;

    /**
     * Constructor
     *
     * @param device   The device alias
     * @param schedule The schedule id of the push notification
     * @param action   The event type
     */
    public HaloPushEvent(String device, String schedule, @NotificationEventsActions.EventType String action) {
        this.device = device;
        this.schedule = schedule;
        this.action = action;
        this.platform = "android";
    }


    /**
     * Create a new instance.
     *
     * @param builder The halo push action event notification builder.
     */
    private HaloPushEvent(@NonNull HaloPushEvent.Builder builder) {
        this.device = builder.device;
        this.schedule = builder.schedule;
        this.action = builder.action;
        this.platform = "android";
    }

    /**
     * Default constructor.
     */
    protected HaloPushEvent() {

    }

    protected HaloPushEvent(Parcel in) {
        device = in.readString();
        schedule = in.readString();
        action = in.readString();
    }

    public static final Creator<HaloPushEvent> CREATOR = new Creator<HaloPushEvent>() {
        @Override
        public HaloPushEvent createFromParcel(Parcel in) {
            return new HaloPushEvent(in);
        }

        @Override
        public HaloPushEvent[] newArray(int size) {
            return new HaloPushEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(device);
        dest.writeString(schedule);
        dest.writeString(action);
    }

    /**
     * Get the device alias.
     *
     * @return The alias
     */
    public String getDevice() {
        return device;
    }

    /**
     * Get the schedule id of the push.
     *
     * @return The schedule id.
     */
    public String getSchedule() {
        return schedule;
    }

    /**
     * Get the action type.
     *
     * @return The action type.
     */
    public String getAction() {
        return action;
    }

    /**
     * Get the platform.
     *
     * @return The platform.
     */
    public String getPlatform() {
        return platform;
    }


    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<HaloPushEvent> {

        /**
         * The device alias.
         */
        private String device;

        /**
         * The schedule id of the push notification.
         */
        private String schedule;

        /**
         * The event action type.
         */
        @NotificationEventsActions.EventType
        private String action;

        /**
         * Creates the builder.
         *
         * @param device The device alias.
         */
        public Builder(String device) {
            this.device = device;
        }


        /**
         * Add a schedule id.
         *
         * @param schedule The schedule id.
         * @return The builder
         */
        @NonNull
        @Api(2.4)
        public Builder withSchedule(String schedule) {
            this.schedule = schedule;
            return this;
        }

        /**
         * Add an action.
         *
         * @param action The action
         * @return The builder.
         */
        @NonNull
        @Api(2.4)
        public Builder withAction(@NotificationEventsActions.EventType String action) {
            this.action = action;
            return this;
        }

        @NonNull
        @Override
        public HaloPushEvent build() {
            return new HaloPushEvent(this);
        }
    }

}
