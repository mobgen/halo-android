package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Users should use this to make own conditions for job by extending this class.
 * {@link #getAction()} needs user to return the broadcast's actions，it can be one or more,
 * {@link HaloSchedulerService} will register receivers for each of them, and then
 * {@link #satisfy(Context, Intent)} give user a change by handing the data from {@link #onReceive(Context, Intent)}
 * here and let user to judge whether the data can satisfied your trigger, then it will be
 * recorded in the Job's status.
 */
public abstract class Trigger implements Receiver {
    /**
     * The id.
     */
    private String mIdentify;

    @Override
    public String getIdentify() {
        if (mIdentify == null) {
            StringBuilder sb = new StringBuilder();
            for (String act : getAction()) {
                sb.append(act).append("|");
            }
            sb.deleteCharAt(sb.length() - 1);
            mIdentify = sb.toString();
        }
        return mIdentify;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TriggerDesc condition = new TriggerDesc(getIdentify());
        condition.satisfy(satisfy(context, intent));
        Intent data = HaloSchedulerService.newIntent(context, condition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (HaloUtils.isServiceRunning(context, HaloSchedulerService.class)) {
                context.startService(data.putExtra(HaloSchedulerService.SERVICE_FOREGROUND, false));
            } else {
                context.startForegroundService(data.putExtra(HaloSchedulerService.SERVICE_FOREGROUND, true));
            }
        } else {
            context.startService(data.putExtra(HaloSchedulerService.SERVICE_FOREGROUND, false));
        }

    }

    /**
     * Provides the action strings.
     *
     * @return The actions for the broadcast.
     */
    @NonNull
    public abstract String[] getAction();

    /**
     * Checks if the trigger is being satisfied.
     *
     * @param context The context.
     * @param intent  The intent for the trigger.
     * @return True if it is satsified, false otherwise.
     */
    protected boolean satisfy(@NonNull Context context, @Nullable Intent intent) {
        return true;
    }
}
