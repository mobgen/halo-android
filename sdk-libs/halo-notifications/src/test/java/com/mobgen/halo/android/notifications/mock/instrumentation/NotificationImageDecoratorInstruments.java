package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSource;
import okio.Okio;

public class NotificationImageDecoratorInstruments {

    public static String givenAImageNotification(Context context, String type) throws IOException {
        InputStream in = context.getClass().getClassLoader().getResourceAsStream(type+"Notification.json");
        BufferedSource buffer = Okio.buffer(Okio.source(in));
        return buffer.readUtf8();
    }

}
