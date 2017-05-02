package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;
import com.mobgen.halo.android.testing.CallbackFlag;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NotificationListenerInstruments {

    public static HaloNotificationListener givenANotificationListener(@NotNull final HaloNotificationsApi api, @NonNull final CallbackFlag flag, final boolean isSilent) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isEqualTo(NotificationInstruments.NOTIFICATION_FROM);
                if (isSilent) {
                    assertThat(api.getNotificationId(data)).isNull();
                } else {
                    assertThat(api.getNotificationId(data)).isNotNull();
                }
                if(data.getString("extra")!=null){

                    try{
                        JSONObject jsonExtra = new JSONObject(data.getString("extra"));
                        assertThat(data.getString("instanceId")).isEqualTo("58594e203bb27211009ccc58");
                    } catch (JSONException jsonException){
                        assertThat(data.getString("extra")).isEqualTo("myextradata");
                    }
                }
            }
        };
    }

    public static HaloNotificationListener givenAnAllNotificationListener(@NonNull final CallbackFlag flag) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
            }
        };
    }

    public static HaloNotificationListener givenATwoFactorListener(@NonNull final CallbackFlag flag) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
            }
        };
    }

    public static HaloNotificationListener givenAnNotificationListenerWithHaloNotificationId(@NonNull final CallbackFlag flag, final String notificationId) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
                assertThat(data.getString("halo_ui_notification_id")).isEqualTo(notificationId);
            }
        };
    }

    public static HaloNotificationListener givenAnNotificationListenerWithCustomId(@NonNull final CallbackFlag flag, final String notificationId) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
                assertThat(data.getBoolean("modifyBundle")).isTrue();
                assertThat(data.getString("halo_ui_notification_id")).isEqualTo(notificationId);
            }
        };
    }
}
