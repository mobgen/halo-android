package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Idle status monitor
 */
final class StatusIdleController implements StatusController {
    /**
     * Tag for the controller.
     */
    static final String TAG = "StatusIdleController";
    /**
     * Sets the debug variable.
     */
    static final boolean DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
    /**
     * Action id to trigger idle status.
     */
    static final String ACTION_TRIGGER_IDLE = "com.mobgen.halo.android.framework.toolbox.scheduler.IDLE_STATUS_CHANGE";
    /**
     * Threshold for idle inactivity.
     */
    private static final long INACTIVITY_IDLE_THRESHOLD = 71 * 60 * 1000; // millis; 71 min
    /**
     * Idle window.
     */
    private static final long IDLE_WINDOW_SLOP = 5 * 60 * 1000; // 5 minute window, to be nice

    /**
     * The service context.
     */
    private Context mContext;
    /**
     * The idle tracker.
     */
    private IdlenessTracker mTracker;

    @Override
    public void onCreate(@NonNull Context context) {
        mContext = context;
        mTracker = new IdlenessTracker();
        mTracker.startTracking();
    }

    @Override
    public void onDestroy() {
        mTracker.stopTracking();
        mTracker = null;
        mContext = null;
    }

    /**
     * Report a new idle state to the service and change the device status.
     *
     * @param idle True if idle, false otherwise.
     */
    private void reportNewIdleState(boolean idle) {
        StatusDevice.IDLE_CONSTRAINT_SATISFIED.set(idle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (HaloUtils.isServiceRunning(mContext, HaloSchedulerService.class)) {
                mContext.startService(HaloSchedulerService.deviceStatusChanged(mContext, Job.STATUS_IDLE_DEVICE_KEY, false));
            } else {
                mContext.startForegroundService(HaloSchedulerService.deviceStatusChanged(mContext, Job.STATUS_IDLE_DEVICE_KEY, true));
            }
        } else {
            mContext.startService(HaloSchedulerService.deviceStatusChanged(mContext, Job.STATUS_IDLE_DEVICE_KEY, false));
        }
    }

    /**
     * Idle tracker to track how much the device has been without usage.
     */
    private class IdlenessTracker extends BroadcastReceiver {
        /**
         * The device is idle.
         */
        private boolean mIdle;
        /**
         * Alarm manager to track the idleness.
         */
        private AlarmManager mAlarm;
        /**
         * Pending intent.
         */
        private PendingIntent mIdleTriggerIntent;

        /**
         * Tracker consrtuctor.
         */
        public IdlenessTracker() {
            mAlarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(ACTION_TRIGGER_IDLE)
                    .setPackage("android")
                    .setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            mIdleTriggerIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

            // At boot we presume that the user has just "interacted" with the
            // device in some meaningful way.
            mIdle = false;
        }

        /**
         * Tells if it is idle.
         *
         * @return True if idle, false otherwise.
         */
        public boolean isIdle() {
            return mIdle;
        }

        /**
         * Starts tracking the idleness.
         */
        public void startTracking() {
            IntentFilter filter = new IntentFilter();

            // Screen state
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);

            // Dreaming state
            if (Build.VERSION.SDK_INT >= 17) {
                filter.addAction(Intent.ACTION_DREAMING_STARTED);
                filter.addAction(Intent.ACTION_DREAMING_STOPPED);
            }

            // Debugging/instrumentation
            filter.addAction(ACTION_TRIGGER_IDLE);

            mContext.registerReceiver(this, filter);
        }

        /**
         * Stops tracking the idleness.
         */
        public void stopTracking() {
            mContext.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                case Intent.ACTION_DREAMING_STOPPED:
                    // possible transition to not-idle
                    if (mIdle) {
                        if (DEBUG) {
                            Halog.d(getClass(), "Exiting idle mode : " + action);
                        }
                        mAlarm.cancel(mIdleTriggerIntent);
                        mIdle = false;
                        reportNewIdleState(false);
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                case Intent.ACTION_DREAMING_STARTED:
                    // when the screen goes off or dreaming starts, we schedule the
                    // alarm that will tell us when we have decided the device is
                    // truly idle.
                    final long nowElapsed = SystemClock.elapsedRealtime();
                    final long when = nowElapsed + INACTIVITY_IDLE_THRESHOLD;
                    if (DEBUG) {
                        Halog.d(getClass(), "Scheduling idle : " + action + " now:" + nowElapsed + " when: " + when);
                    }
                    if (Build.VERSION.SDK_INT >= 19) {
                        mAlarm.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, when, IDLE_WINDOW_SLOP, mIdleTriggerIntent);
                    } else {
                        mAlarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, when, mIdleTriggerIntent);
                    }
                    break;
                case ACTION_TRIGGER_IDLE:
                    // idle time starts now
                    if (!mIdle) {
                        if (DEBUG) {
                            Halog.d(getClass(), "Idle trigger fired @ " + SystemClock.elapsedRealtime());
                        }
                        mIdle = true;
                        reportNewIdleState(true);
                    }
                    break;
            }
        }
    }
}
