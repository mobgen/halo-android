package com.mobgen.halo.android.app.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Broadcast receiver to listen for configuration changes.
 */
public class MessagesNotificationReceiver extends BroadcastReceiver {

    public interface MessageReceiveListener {
        void onNewMessage(Intent intent);
    }

    private MessageReceiveListener mListener;

    public MessagesNotificationReceiver() {

    }

    public MessagesNotificationReceiver(@NonNull MessageReceiveListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mListener.onNewMessage(intent);
    }
}
