package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;


/**
 * Listening device charge status changing
 */
final class StatusChargingController implements StatusController {
    /**
     * The current context.
     */
    private Context mContext;
    /**
     * The receiver that checks for charging.
     */
    private ChargingReceiver mChargeReceiver;

    @Override
    public void onCreate(@NonNull Context context) {
        mContext = context;
        updateStatus();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mChargeReceiver = new ChargingReceiver();
        context.registerReceiver(mChargeReceiver, filter);
    }

    /**
     * Parses the status given an intent.
     */
    public void updateStatus() {
        //Set the device status value
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, filter);
        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;
            StatusDevice.CHARGING_CONSTRAINT_SATISFIED.set(isCharging);
        }
    }

    @Override
    public void onDestroy() {
        //Unregister the receiver
        mContext.unregisterReceiver(mChargeReceiver);
        mChargeReceiver = null;
        mContext = null;
    }

    /**
     * Broadcast receiver to check status changes on the battery and
     * charge triggers.
     */
    private class ChargingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(HaloSchedulerService.deviceStatusChanged(context, Job.STATUS_CHARGING_KEY, true));
            } else {
                context.startService(HaloSchedulerService.deviceStatusChanged(context, Job.STATUS_CHARGING_KEY, false));
            }
        }
    }
}
