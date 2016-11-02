package com.mobgen.halo.android.app.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;


import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;

import java.util.Locale;

/**
 * Handles the silent notifications for halo.
 */
public class SilentNotificationDispatcher implements HaloNotificationListener {

    @Override
    public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
        if (extra != null) {
            String moduleEvent = extra.getString("module");
            if (moduleEvent != null) {
                Intent intent = new Intent(moduleEvent + "-notification");
                intent.putExtras(extra);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        } else {
            String title = data.getString("body");
            if (title != null) {
                Intent intent = new Intent(title.toLowerCase(Locale.getDefault()));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
    }
}
