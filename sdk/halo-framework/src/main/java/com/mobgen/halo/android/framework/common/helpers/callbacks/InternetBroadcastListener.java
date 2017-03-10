package com.mobgen.halo.android.framework.common.helpers.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Listens for internet ant when it is ready the callback is dispatched.
 */
public class InternetBroadcastListener extends BroadcastReceiver {

    /**
     * The internet listener.
     */
    private final InternetListener mListener;
    /**
     * The application context.
     */
    private Context mContext;

    /**
     * Constructor with the listener.
     *
     * @param context  The context to listen.
     * @param listener The listener.
     */
    private InternetBroadcastListener(@NonNull Context context, @NonNull InternetListener listener) {
        AssertionUtils.notNull(context, "context");
        AssertionUtils.notNull(listener, "listener");
        mContext = context;
        mListener = listener;
    }

    /**
     * Start listening on the given context for internet connection.
     *
     * @param context  The context to listen.
     * @param listener The listener.
     * @return The broadcast listener.
     */
    @Api(2.0)
    @NonNull
    @CheckResult(suggest = "listener.unlisten to avoid dropping the listener")
    public static InternetBroadcastListener listen(@NonNull Context context, @NonNull InternetListener listener) {
        InternetBroadcastListener broadcastListener = new InternetBroadcastListener(context, listener);
        context.registerReceiver(broadcastListener, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return broadcastListener;
    }

    /**
     * Stops listening the context.
     *
     */
    @Api(2.3)
    public void unlisten() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (HaloUtils.isNetworkConnected(context)) {
            mListener.onInternetReady();
        }
    }

    /**
     * Internet broadcast listener. It receives a callback when the internet
     * connection is ready,
     */
    public interface InternetListener {
        /**
         * Callback that notifies when the internet is ready.
         */
        @Api(2.0)
        void onInternetReady();
    }
}
