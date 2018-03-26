package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Listening device network status changing
 */
final class StatusNetworkController implements StatusController {
    /**
     * The connectivity manager.
     */
    private ConnectivityManager mManager;
    /**
     * The context.
     */
    private Context mContext;
    /**
     * The connection broadcast receiver.
     */
    private ConnectionReceiver mConnectionReceiver;

    @Override
    public void onCreate(@NonNull Context context) {
        mContext = context;
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //update related status ASAP
        updateStatus();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mConnectionReceiver = new ConnectionReceiver();
        context.registerReceiver(mConnectionReceiver, filter);
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mConnectionReceiver);
        mManager = null;
        mContext = null;
        mConnectionReceiver = null;
    }

    /**
     * Parses the status given an intent.
     */
    private void updateStatus() {
        NetworkInfo info = mManager.getActiveNetworkInfo();
        boolean connected = false;
        if (info != null && info.isAvailable()) {
            connected = info.isConnectedOrConnecting();
        }
        //Set the params for the network.
        StatusDevice.CONNECTIVITY_CONSTRAINT_SATISFIED.set(connected);
        StatusDevice.UNMETERED_CONSTRAINT_SATISFIED.set(connected && !isNetworkMetered());
    }

    /**
     * Checks if the network is one that may cause charges from the carrier.
     *
     * @return True if the network is metered, false otherwise.
     */
    private boolean isNetworkMetered() {
        if (mManager.getActiveNetworkInfo() == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            return mManager.isActiveNetworkMetered();
        } else {
            return mManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE;
        }
    }

    /**
     * Connection receiver to check network updates.
     */
    private class ConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (HaloUtils.isServiceRunning(context, HaloSchedulerService.class)) {
                    context.startService(HaloSchedulerService.deviceStatusChanged(context, Job.STATUS_NETWORK_TYPE_KEY, false));
                } else {
                    context.startForegroundService(HaloSchedulerService.deviceStatusChanged(context, Job.STATUS_NETWORK_TYPE_KEY, true));
                }
            } else {
                context.startService(HaloSchedulerService.deviceStatusChanged(context, Job.STATUS_NETWORK_TYPE_KEY, false));
            }
        }
    }
}
