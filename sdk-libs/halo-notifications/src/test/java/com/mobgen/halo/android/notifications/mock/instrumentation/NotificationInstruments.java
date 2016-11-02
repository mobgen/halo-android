package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.support.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class NotificationInstruments {

    public static final String NOTIFICATION_NAME = "notification_sample";
    public static final String NOTIFICATION_FROM = "notification_from";

    @NonNull
    public static RemoteMessage givenANotification(Map<String, String> map) throws NoSuchFieldException, IllegalAccessException {
        return new RemoteMessage.Builder(NOTIFICATION_NAME)
                .setMessageId("id")
                .setMessageType("sample")
                .setData(map)
                .build();
    }

    @NonNull
    public static Map<String, String> withSilentNotification() {
        Map<String, String> map = new HashMap<>();
        map.put("content_available", "1");
        map.put("from", NOTIFICATION_FROM);
        return map;
    }

    @NonNull
    public static Map<String, String> withNotSilentNotification() {
        Map<String, String> map = new HashMap<>();
        map.put("content_available", "0");
        map.put("from", NOTIFICATION_FROM);
        return map;
    }

    @NonNull
    public static Map<String, String> withAnySourceNotification() {
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        return map;
    }
}
