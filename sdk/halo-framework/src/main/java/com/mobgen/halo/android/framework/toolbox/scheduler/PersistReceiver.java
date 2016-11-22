package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * If you want persist your jobs after device rebooting,
 * enable this by declare this class in you AndroidManifest.xml
 * with the action:
 * <p>
 * android.intent.action.BOOT_COMPLETED
 */
public class PersistReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case HaloJobScheduler.DEBUG_DEVICE_ON_B:
                if (!HaloJobScheduler.DEBUG) {
                    break;
                }
            case Intent.ACTION_BOOT_COMPLETED:
                context.startService(HaloSchedulerService.deviceOn(context));
                break;
            default:
                break;
        }
    }
}
