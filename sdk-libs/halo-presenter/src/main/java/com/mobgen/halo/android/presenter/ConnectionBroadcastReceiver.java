package com.mobgen.halo.android.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Broadcast receiver that helps with the task of network status management.
 */
public class ConnectionBroadcastReceiver extends BroadcastReceiver {

    /**
     * Listener that is in charge to manage the state of the network connectivity.
     */
    public interface NetworkStateListener {

        /**
         * Determines which is the state of the network in a given moment.
         *
         * @param connected True if the network is connected, false if it is not
         *                  or the permission android.permission.ACCESS_NETWORK_STATE is not granted
         *                  in the manifest.
         */
        void onNetworkStateChangedTo(boolean connected);
    }

    /**
     * The listener of the network state.
     */
    private NetworkStateListener mListener;

    /**
     * Constructor of the network broadcast receiver.
     *
     * @param listener The listener to emit the network state when it is modified.
     */
    public ConnectionBroadcastReceiver(@Nullable NetworkStateListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {
            //Notify
            mListener.onNetworkStateChangedTo(HaloUtils.isNetworkConnected(context));
        }
    }
}
