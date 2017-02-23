package com.mobgen.halo.android.notifications.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;

/**
 * @hide Emitter that allows Halo and the items related to it to listen for notifications
 * and events related to notification services.
 */
public final class NotificationEmitter {

    /**
     * Intent filter to refresh the token.
     */
    private static final String NOTIFICATION_ALL_NOTIFICATIONS = ":halo:notifications:all";
    /**
     * Intent filter to refresh the token.
     */
    private static final String NOTIFICATION_NOT_SILENT_NOTIFICATIONS = ":halo:notifications:not_silent";
    /**
     * Intent filter to refresh the token.
     */
    private static final String NOTIFICATION_TWO_FACTOR = ":halo:notifications:two_factor";
    /**
     * Intent filter to refresh the token.
     */
    private static final String NOTIFICATION_SILENT_NOTIFICATIONS = ":halo:notifications:silent";
    /**
     * Intent filter to refresh the token.
     */
    private static final String NOTIFICATION_REFRESH_TOKEN = ":halo:notifications:refresh_gcm_token";
    /**
     * Extras bundle name.
     */
    private static final String EXTRAS_BUNDLE = "extra";
    /**
     * Extras bundle name.
     */
    private static final String FROM_BUNDLE = "halo:notification:from";

    /**
     *
     */
    private NotificationEmitter() {

    }

    /**
     * Provides the filter for the event id provided.
     *
     * @param context The context.
     * @param eventId The event id.
     * @return The intent filter.
     */
    @NonNull
    private static IntentFilter getFilterFor(@NonNull Context context, @NonNull String eventId) {
        return new IntentFilter(HaloUtils.getEventName(context, eventId));
    }


    /**
     * Creates an intent given the parameters to emit a notification.
     *
     * @param context The context.
     * @param from    The from location for the notification.
     * @param data    The data for the notification.
     * @param eventId The event id.
     * @return The intent created.
     */
    @NonNull
    private static Intent getIntentFor(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @NonNull String eventId) {
        Intent intent = new Intent(HaloUtils.getEventName(context, eventId));
        data.putString(FROM_BUNDLE, from);
        intent.putExtras(data);
        return intent;
    }

    /**
     * Listen for all the notifications.
     *
     * @param context  The context.
     * @param listener The listener.
     * @return The subscription created.
     */
    @NonNull
    @CheckResult(suggest = "Keep a reference to unsubscribe.")
    public static ISubscription createAllNotificationSubscription(@NonNull Context context, @NonNull HaloNotificationListener listener) {
        return new ReceiverSubscription(context, listener, getFilterFor(context, NOTIFICATION_ALL_NOTIFICATIONS));
    }

    /**
     * Emits a all broadcast for the notification provided.
     *
     * @param context The context.
     * @param from    The source of the notification.
     * @param data    The data created.
     */
    public static void emitAll(@NonNull Context context, @NonNull String from, @NonNull Bundle data) {
        context.sendBroadcast(getIntentFor(context, from, data, NOTIFICATION_ALL_NOTIFICATIONS));
    }

    /**
     * Listen for all the notifications.
     *
     * @param context  The context.
     * @param listener The listener.
     * @return The subscription created.
     */
    @NonNull
    @CheckResult(suggest = "Keep a reference to unsubscribe.")
    public static ISubscription createNotSilentNotificationSubscription(@NonNull Context context, @NonNull HaloNotificationListener listener) {
        return new ReceiverSubscription(context, listener, getFilterFor(context, NOTIFICATION_NOT_SILENT_NOTIFICATIONS));
    }

    /**
     * Emits a not silent broadcast for the notification provided.
     *
     * @param context The context.
     * @param from    The source of the notification.
     * @param data    The data created.
     */
    public static void emitNotSilent(@NonNull Context context, @NonNull String from, @NonNull Bundle data) {
        context.sendBroadcast(getIntentFor(context, from, data, NOTIFICATION_NOT_SILENT_NOTIFICATIONS));
    }


    @NonNull
    @CheckResult(suggest = "Keep a reference to unsubscribe.")
    public static ISubscription createTwoFActorSubscription(@NonNull Context context, @NonNull HaloNotificationListener listener) {
        return new ReceiverSubscription(context, listener, getFilterFor(context, NOTIFICATION_TWO_FACTOR));
    }

    public static void emitTwoFactor(@NonNull Context context, @NonNull String from, @NonNull Bundle data) {
        context.sendBroadcast(getIntentFor(context, from, data, NOTIFICATION_TWO_FACTOR));
    }

    /**
     * Listen for all the notifications.
     *
     * @param context  The context.
     * @param listener The listener.
     * @return The subscription created.
     */
    @NonNull
    @CheckResult(suggest = "Keep a reference to unsubscribe")
    public static ISubscription createSilentNotificationSubscription(@NonNull Context context, @NonNull HaloNotificationListener listener) {
        return new ReceiverSubscription(context, listener, getFilterFor(context, NOTIFICATION_SILENT_NOTIFICATIONS));
    }

    /**
     * Emits a silent broadcast for the notification provided.
     *
     * @param context The context.
     * @param from    The source of the notification.
     * @param data    The data created.
     */
    public static void emitSilent(@NonNull Context context, @NonNull String from, @NonNull Bundle data) {
        context.sendBroadcast(getIntentFor(context, from, data, NOTIFICATION_SILENT_NOTIFICATIONS));
    }

    /**
     * Listens for the refresh token event.
     *
     * @param context  The context.
     * @param listener The listener.
     * @return The subscription.
     */
    @NonNull
    public static ISubscription listenRefreshTokenEvent(@NonNull Context context, @NonNull InstanceIDService.RefreshNotificationTokenListener listener) {
        RefreshTokenReceiverSubscription refreshToken = new RefreshTokenReceiverSubscription(context, listener);
        context.registerReceiver(refreshToken, getFilterFor(context, NOTIFICATION_REFRESH_TOKEN));
        return refreshToken;
    }

    /**
     * Emits a refresh token event.
     *
     * @param context The context.
     */
    public static void emitRefreshToken(@NonNull Context context) {
        Intent intent = new Intent(HaloUtils.getEventName(context, NOTIFICATION_REFRESH_TOKEN));
        context.sendBroadcast(intent);
    }

    /**
     * Receiver subscription for the notifications.
     */
    private static class ReceiverSubscription extends BroadcastReceiver implements ISubscription {

        /**
         * The context.
         */
        @NonNull
        private Context mContext;
        /**
         * The listener for this subscription.
         */
        @NonNull
        private HaloNotificationListener mListener;

        /**
         * Constructor for the receiver.
         *
         * @param context  The context.
         * @param listener The listener.
         * @param filter   A filter to register the receiver.
         */
        public ReceiverSubscription(@NonNull Context context, @NonNull HaloNotificationListener listener, @NonNull IntentFilter filter) {
            mContext = context;
            mListener = listener;
            context.registerReceiver(this, filter);
        }

        @Override
        public void unsubscribe() {
            mContext.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String from = bundle.getString(FROM_BUNDLE);
            if (from == null) {
                from = "";
            }
            Bundle extras = bundle.getBundle(EXTRAS_BUNDLE);
            mListener.onNotificationReceived(context, from, bundle, extras);
        }
    }

    /**
     * Receiver subscription to refresh the token.
     */
    private static class RefreshTokenReceiverSubscription extends BroadcastReceiver implements ISubscription {

        /**
         * The context.
         */
        @NonNull
        private Context mContext;
        /**
         * The listener for this subscription.
         */
        @NonNull
        private InstanceIDService.RefreshNotificationTokenListener mListener;

        /**
         * Constructor for the receiver.
         *
         * @param context  The context.
         * @param listener The listener.
         */
        public RefreshTokenReceiverSubscription(@NonNull Context context, @NonNull InstanceIDService.RefreshNotificationTokenListener listener) {
            mContext = context;
            mListener = listener;
        }

        @Override
        public void unsubscribe() {
            mContext.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mListener.onRefreshToken();
        }
    }
}
