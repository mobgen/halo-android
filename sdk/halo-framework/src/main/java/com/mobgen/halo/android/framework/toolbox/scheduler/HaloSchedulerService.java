package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.storage.preference.HaloPreferencesStorage;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mobgen.halo.android.framework.api.HaloConfig.SERVICE_NOTIFICATION_CHANNEL;
import static com.mobgen.halo.android.framework.api.StorageConfig.DEFAULT_STORAGE_NAME;

/**
 * HaloJobScheduler loop, all jobs finally get in here and be managed, checked and executed.
 */
public final class HaloSchedulerService extends Service {

    /**
     * Conditions intent param.
     */
    static final String CONDITION_DATA = "condition_data";
    /**
     * Protocol key intent param.
     */
    static final String PROTOCOL_KEY = "protocol_key";
    /**
     * The device key intent param.
     */
    static final String BOOT_DEVICE_KEY = "device_key";
    /**
     * The status changed intent param.
     */
    static final String STATUS_CHANGED = "status_changed";
    /**
     * Protocol magic code.
     */
    static final int PROTOCOL_CODE = 0x991;

    /**
     * The notification id for android sdk target 26 +
     */
    static final int FOREGROUND_NOTIFICATION_ID = 0x091;

    /**
     * The notification channel id for android sdk target 26 +
     */
    static final String FOREGROUND_CHANNEL_NOTIFICATION_ID = "halo_hotfix_android_O_26";

    /**
     * Tag for the logger.
     */
    private static final String TAG = "HaloSchedulerService";
    /**
     * Deadline broadcast.
     */
    private static final String DEADLINE_BROADCAST = "com.mobgen.halo.android.framework.toolbox.scheduler.DEADLINE_CHECK";
    /**
     * Dir for persisting.
     */
    private static final String JOB_PERSIST_DIR = "job_persist";
    /**
     * Dir for the backups.
     */
    private static final String JOB_BACKUP_DIR = "job_backup";

    /**
     * The jobs to be triggered.
     */
    private ConcurrentHashMap<String, Job> mJobsSet;
    /**
     * The receivers.
     */
    private ConcurrentHashMap<String, BroadcastReceiver> mReceivers;
    /**
     * Hanlder that dispatches the messages to perform checks.
     */
    private CheckHandler mChecker;
    /**
     * The thread manager.
     */
    private HaloThreadManager mThreadManager;
    /**
     * The alarm manager to perform triggers.
     */
    private AlarmManager mAlarmManager;
    /**
     * The handler for the deadlines.
     */
    private Handler mShortDeadlineHandler;
    /**
     * The deadline checker.
     */
    private DeadlineCheck mDeadlineCheck;
    /**
     * The wakelock aquired.
     */
    private PowerManager.WakeLock mWakeLock;
    /**
     * The device status.
     */
    private StatusDevice mDeviceStatus;
    /**
     * A handler thread.
     */
    private HandlerThread mHandlerThread;

    /**
     * Handle when a service come from boot
     */
    private boolean shouldHideNotification = true;

    /**
     * The scheduler binder stub.
     */
    private final IHaloSchedulerServiceBinder.Stub mBinder = new IHaloSchedulerServiceBinder.Stub() {

        @Override
        public IBinder asBinder() {
            return this;
        }

        /**
         * Schedule a new job into the service.
         *
         * @param job The job.
         */
        @Override
        public void schedule(@NonNull Job job) {
            addJob(job, true);
        }

        /**
         * Cancel the job by tag.
         *
         * @param tag The tag.
         */
        @Override
        public void cancel(String tag) {
            removeJob(tag);
        }

        /**
         * Removes a persisiting job.
         *
         * @param tag The tag.
         */
        @Override
        public void removePersistJob(String tag) {
            mChecker.removePersistJobWithTag(tag);
        }

        /**
         * Stop the service and reset all the variables.
         */
        @Override
        public void stopAndReset() {
            cleanUpAll();
        }

        /**
         * Sets the thread manager.
         *
         * @param threadManager The thread manager.
         */
        @Override
        public void threadManager(@NonNull HaloThreadManager threadManager) {
            mThreadManager = threadManager;
        }
    };

    /**
     * Creates a new intent.
     *
     * @param context The context.
     * @return The intent.
     */
    @NonNull
    static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, HaloSchedulerService.class);
        intent.putExtra(PROTOCOL_KEY, PROTOCOL_CODE);
        return intent;
    }

    /**
     * The new intent.
     *
     * @param context   The context.
     * @param condition A trigger for this intent.
     * @return The intent created.
     */
    @NonNull
    static Intent newIntent(@NonNull Context context, @NonNull TriggerDesc condition) {
        Intent data = newIntent(context);
        data.putExtra(CONDITION_DATA, condition);
        return data;
    }

    /**
     * Device started intent.
     *
     * @param context The context.
     * @return The intent.
     */
    @NonNull
    static Intent deviceOn(@NonNull Context context) {
        Intent data = newIntent(context);
        data.putExtra(BOOT_DEVICE_KEY, true);
        return data;
    }

    /**
     * Intent for device status changes.
     *
     * @param context The context.
     * @param which   which is the status change.
     * @return The intent created.
     */
    @NonNull
    static Intent deviceStatusChanged(Context context, String which) {
        Intent data = newIntent(context);
        data.putExtra(STATUS_CHANGED, which);
        return data;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        foregroundNotification();
        mJobsSet = new ConcurrentHashMap<>();
        mReceivers = new ConcurrentHashMap<>();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mShortDeadlineHandler = new Handler();
        mDeadlineCheck = new DeadlineCheck();
        mDeviceStatus = StatusDevice.get(this);
        registerReceiver(mDeadlineCheck, new IntentFilter(DEADLINE_BROADCAST));
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        //Get the wakelock if available
        int granted = checkCallingOrSelfPermission(Manifest.permission.WAKE_LOCK);
        if (granted == PackageManager.PERMISSION_GRANTED) {
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        } else {
            mWakeLock = null;
        }

        //Handler thread
        mHandlerThread = new HandlerThread("HaloJobScheduler-HandlerThread");
        mHandlerThread.start();
        mChecker = new CheckHandler(mHandlerThread.getLooper());
        //TODO move from main thread
        mayRecoverJobsFromFile();
    }

    /**
     * Checks if there are jobs that must be recovered form a file that is persisted.
     */
    private void mayRecoverJobsFromFile() {
        File recoverDir = new File(getFilesDir(), JOB_BACKUP_DIR);
        if (!recoverDir.exists()) {
            return;
        }
        if (recoverDir.listFiles() == null) {
            return;
        }
        for (File file : recoverDir.listFiles()) {
            Job job = null;
            try {
                job = Job.createJobFromPersistInfo(Job.JobInfo.readFromFile(file));
            } catch (IOException e) {
                Halog.e(getClass(), "The job could not be recreated: " + file.getName(), e);
            }
            file.delete();
            if (job != null) {
                addJob(job, true);
            }
        }
    }

    /**
     * Tries to create a backup from the given job.
     *
     * @param job The job.
     */
    private void tryCreateBackups(@NonNull Job job) {
        //TODO move from main thread
        if (!job.canBePersisted()) {
            return;
        }
        File recoverDir = new File(getFilesDir(), JOB_BACKUP_DIR);
        if (!recoverDir.exists()) {
            recoverDir.mkdirs();
        }
        try {
            job.info().writeToFile(recoverDir);
        } catch (IOException e) {
            Halog.e(getClass(), "Can not create backup for job: " + job.info().mIdentity);
        }
        //try to persist job that needs to be valid after reboot
        if (!job.info().mPersistAfterReboot) {
            return;
        }
        //TODO move from main thread
        final File dir = new File(getFilesDir(), JOB_PERSIST_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            job.info().writeToFile(dir);
        } catch (IOException ignore) {
            Halog.e(getClass(), "Can not persist the job: " + job.info().mIdentity);
        }
    }

    /**
     * Deletes the backup for the given job.
     *
     * @param job The job.
     */
    private void deleteBackup(@NonNull Job job) {
        //TODO move from main thread
        File recoverDir = new File(getFilesDir(), JOB_BACKUP_DIR);
        if (!recoverDir.exists())
            return;
        job.info().tryDelete(recoverDir);
        final File dir = new File(getFilesDir(), JOB_PERSIST_DIR);
        if (!dir.exists()) {
            return;
        }
        if (!job.info().mPersistAfterReboot)
            return;
        job.info().tryDelete(dir);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceStatus.onDestroy();
        mHandlerThread.quit();
        mThreadManager = null;
        unregisterReceiver(mDeadlineCheck);
    }

    /**
     * Tries to get the wakelock. Only available if the permission is there.
     */
    private void tryAcquireLock() {
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    /**
     * Tries to release the wakelock. Only available if the permission is there.
     */
    private void tryReleaseLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

    /**
     * Adds a new job and triggers it if available.
     *
     * @param job        The job to add.
     * @param mayTrigger True to trigger it. False otherwise.
     */
    void addJob(@Nullable Job job, boolean mayTrigger) {
        if (job == null) {
            return;
        }

        //Avoid duplicate job
        if (job.info().mPersistAfterReboot) {
            for (String jk : mJobsSet.keySet()) {
                Job existJob = mJobsSet.get(jk);
                if (existJob.equals(job)) {
                    return;
                }
            }
        }
        mJobsSet.put(job.info().mIdentity, job);
        tryCreateBackups(job);

        // Receiver handle
        for (Trigger trigger : job.triggers()) {
            if (!mReceivers.containsKey(trigger.getIdentify())) {
                IntentFilter filter = new IntentFilter();
                for (String act : trigger.getAction()) {
                    filter.addAction(act);
                }
                ReceiverInner rec = new ReceiverInner(trigger);
                registerReceiver(rec, filter);
                mReceivers.put(trigger.getIdentify(), rec);
            }
        }

        // Can it happen now?
        if (mayTrigger) {
            //Update the default conditions
            if (job.conditionsOk().containsKey(Job.STATUS_CHARGING_KEY)) {
                job.conditionsOk().put(Job.STATUS_CHARGING_KEY, StatusDevice.CHARGING_CONSTRAINT_SATISFIED.get());
            }
            if (job.conditionsOk().containsKey(Job.STATUS_NETWORK_TYPE_KEY)) {
                job.conditionsOk().put(Job.STATUS_NETWORK_TYPE_KEY, StatusDevice.networkTypeSatisfied(job.info().mNetworkType));
            }
            if (job.conditionsOk().containsKey(Job.STATUS_IDLE_DEVICE_KEY)) {
                job.conditionsOk().put(Job.STATUS_IDLE_DEVICE_KEY, StatusDevice.IDLE_CONSTRAINT_SATISFIED.get());
            }
            if (mChecker.mayTriggerAfterCheck(job)) {
                return; //Job triggered
            }
        }

        // Job not triggered, deadline handle
        if (job.info().mDeadline != -1L) {
            long df = job.info().mDeadline - System.currentTimeMillis();
            if (df < 0) {
                mChecker.trigger(job);
                return;
            }
            if (df > 60 * 1000) { //Set an alarm for it
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(DEADLINE_BROADCAST), 0);
                if (Build.VERSION.SDK_INT >= 19) {
                    mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, job.info().mDeadline, pendingIntent);
                } else {
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, job.info().mDeadline, pendingIntent);
                }
                job.mDeadLineObj = pendingIntent;
            } else { //Delay instead of adding an alarm
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        mChecker.checkDeadline();
                    }
                };
                job.mDeadLineObj = runnable;
                mShortDeadlineHandler.postDelayed(runnable, df);
            }
        }
    }

    /**
     * Removes the job by the given tag.
     *
     * @param tag The tag.
     */
    void removeJob(@NonNull String tag) {
        ArrayList<String> tobeRemoved = new ArrayList<>();
        for (String key : mJobsSet.keySet()) {
            Job job = mJobsSet.get(key);
            if (job.info().mTag.equals(tag)) {
                tobeRemoved.add(key);
            }
        }
        for (String key : tobeRemoved) {
            removeOne(key);
        }
    }

    /**
     * Removes the job by key.
     *
     * @param key The key.
     */
    void removeOne(@NonNull String key) {
        Job removed = null;
        if (mJobsSet.containsKey(key)) {
            removed = mJobsSet.remove(key);
        }
        if (removed == null) {
            return;
        }
        deleteBackup(removed);
        List<String> removedConds = new ArrayList<>();
        //Get all receivers
        for (Receiver receiver : removed.triggers()) {
            removedConds.add(receiver.getIdentify());
        }

        // If this trigger is present on another job remove it
        for (Map.Entry<String, Job> entry : mJobsSet.entrySet()) {
            for (Receiver condition : entry.getValue().triggers()) {
                if (removedConds.indexOf(condition.getIdentify()) != -1) {
                    removedConds.remove(condition.getIdentify());
                }
            }
        }

        // Unregister the conditions
        for (String k : removedConds) {
            unregisterReceiver(mReceivers.get(k));
            mReceivers.remove(k);
        }

        // Deadline handle
        if (removed.info().mDeadline != -1L) {
            if (removed.mDeadLineObj instanceof PendingIntent) {
                mAlarmManager.cancel((PendingIntent) removed.mDeadLineObj);
            } else if (removed.mDeadLineObj instanceof Runnable) {
                mShortDeadlineHandler.removeCallbacks((Runnable) removed.mDeadLineObj);
            }
        }
    }

    /**
     * Cleans up all the pending jobs.
     */
    void cleanUpAll() {
        mChecker.cleanup();
        for (Map.Entry<String, Job> entry : mJobsSet.entrySet()) {
            Job job = entry.getValue();
            job.resetConds();
            if (job.mDeadLineObj != null) {
                if (job.mDeadLineObj instanceof PendingIntent) {
                    mAlarmManager.cancel((PendingIntent) job.mDeadLineObj);
                } else if (job.mDeadLineObj instanceof Runnable) {
                    mShortDeadlineHandler.removeCallbacks((Runnable) job.mDeadLineObj);
                }
            }
        }
        mJobsSet.clear();
        for (Map.Entry<String, BroadcastReceiver> entry : mReceivers.entrySet()) {
            BroadcastReceiver r = entry.getValue();
            unregisterReceiver(r);
        }
        mReceivers.clear();
        deleteDir(new File(getFilesDir(), JOB_BACKUP_DIR));
        deleteDir(new File(getFilesDir(), JOB_PERSIST_DIR));
    }

    /**
     * Deletes the dir of the given file.
     *
     * @param file The file.
     */
    private void deleteDir(@NonNull File file) {
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                deleteDir(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            hideForegroundNotification();
            return START_NOT_STICKY;
        }
        if (!intent.hasExtra(PROTOCOL_KEY) || intent.getIntExtra(PROTOCOL_KEY, -1) != PROTOCOL_CODE) {
            hideForegroundNotification();
            throw new IllegalAccessError("HaloSchedulerService won't receive user command.");
        }
        if (intent.hasExtra(CONDITION_DATA)) {
            hideForegroundNotification();
            TriggerDesc condition = intent.getParcelableExtra(CONDITION_DATA);
            Halog.d(getClass(), "Trigger received: " + condition.toString());
            mChecker.checkSatisfy(condition);
        } else if (intent.hasExtra(BOOT_DEVICE_KEY)) {
            mChecker.checkDeviceBootCompleted();
        } else if (intent.hasExtra(STATUS_CHANGED)) {
            hideForegroundNotification();
            mChecker.checkStatusChanged(intent.getStringExtra(STATUS_CHANGED));
        }
        return START_NOT_STICKY;
    }

    /**
     * Show a foreground service notification when a device has an api version 26+
     */
    private void foregroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HaloPreferencesStorage preferences = new HaloPreferencesStorage(this, DEFAULT_STORAGE_NAME);
            String channelNotificationName = preferences.getString(SERVICE_NOTIFICATION_CHANNEL, "HALO foreground");

            NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_NOTIFICATION_ID, channelNotificationName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(getApplicationContext(), FOREGROUND_CHANNEL_NOTIFICATION_ID)
                    .setWhen(System.currentTimeMillis())
                    .setChannelId(FOREGROUND_CHANNEL_NOTIFICATION_ID)
                    .build();
            startForeground(FOREGROUND_NOTIFICATION_ID, notification);
        }
    }

    /**
     * Hide the foreground notification because the service is not on background anymore.
     */
    private void hideForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isServiceRunningInForeground(getApplicationContext(), HaloSchedulerService.class)) {
            stopForeground(true);
        }
    }

    /**
     * Check if a service is running in foreground
     *
     * @param context      The context
     * @param serviceClass The service class
     * @return True if its in foreground; False otherwise
     */
    private boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return service.foreground;
            }
        }
        return false;
    }

    /**
     * Handler to check the possible status and job behaviors to see if the the
     * conditions are satisfied.
     */
    private class CheckHandler extends Handler {
        /**
         * Message of satisfaction.
         */
        private final int MSG_TRIGGER_SATISFY = 1;
        /**
         * Message of deadline for the current job.
         */
        private final int MSG_DEADLINE = 2;
        /**
         * The device is on message.
         */
        private final int MSG_BOOT_DEVICE_COMPLETED = 3;
        /**
         * Message for charging.
         */
        private final int MSG_STATUS_CHANGED = 4;
        /**
         * message for removing a job by tag.
         */
        private final int MSG_REMOVE_TAG_JOB = 5;

        /**
         * The looper to which this handler is attached.
         *
         * @param looper The looper.
         */
        public CheckHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TRIGGER_SATISFY:
                    checkTriggerSatisfyImpl((TriggerDesc) msg.obj);
                    break;
                case MSG_DEADLINE:
                    checkDeadlineImpl();
                    break;
                case MSG_BOOT_DEVICE_COMPLETED:
                    checkDeviceOnImpl();
                    break;
                case MSG_STATUS_CHANGED:
                    checkStatusChangedImpl((String) msg.obj);
                    break;
                case MSG_REMOVE_TAG_JOB:
                    removePersistJobWithTagImpl((String) msg.obj);
                    break;
                default:
                    break;
            }
        }

        /**
         * Check the satisfaction for the passed trigger.
         *
         * @param trigger The trigger.
         */
        private void checkTriggerSatisfyImpl(@NonNull TriggerDesc trigger) {
            tryAcquireLock();
            for (Map.Entry<String, Job> entry : mJobsSet.entrySet()) {
                final Job job = entry.getValue();
                boolean hit = true;
                for (String key : job.conditionsOk().keySet()) {
                    if (key.equals(trigger.mIdentity)) {
                        job.conditionsOk().put(key, trigger.mSatisfy);
                    }
                    if (hit) {
                        hit = job.conditionsOk().get(key);
                    }
                }
                if (hit) {
                    trigger(job);
                }
            }
            tryReleaseLock();
        }

        /**
         * Checks the status change implementation based on the type
         * of change that was triggered.
         *
         * @param which The triggered change.
         */
        private void checkStatusChangedImpl(@NonNull final String which) {
            tryAcquireLock();
            //while code runs here, all status have been refreshed, we just check all jobs here and
            //try to find out which can be triggered.
            for (String key : mJobsSet.keySet()) {
                Job job = mJobsSet.get(key);
                switch (which) {
                    case Job.STATUS_CHARGING_KEY:
                        if (job.conditionsOk().containsKey(Job.STATUS_CHARGING_KEY)) {
                            job.conditionsOk().put(Job.STATUS_CHARGING_KEY,
                                    StatusDevice.CHARGING_CONSTRAINT_SATISFIED.get());
                        }
                        break;
                    case Job.STATUS_IDLE_DEVICE_KEY:
                        if (job.conditionsOk().containsKey(Job.STATUS_IDLE_DEVICE_KEY)) {
                            job.conditionsOk().put(Job.STATUS_IDLE_DEVICE_KEY,
                                    StatusDevice.IDLE_CONSTRAINT_SATISFIED.get());
                        }
                        break;
                    case Job.STATUS_NETWORK_TYPE_KEY:
                        if (job.conditionsOk().containsKey(Job.STATUS_NETWORK_TYPE_KEY)) {
                            job.conditionsOk().put(Job.STATUS_NETWORK_TYPE_KEY,
                                    StatusDevice.networkTypeSatisfied(job.info().mNetworkType));
                        }
                        break;
                    default:
                        break;
                }
                if (job.conditionsOk().containsKey(which)) {
                    mayTriggerAfterCheck(job);
                }
            }
            tryReleaseLock();
        }

        /**
         * Checks the deadline.
         */
        private void checkDeadlineImpl() {
            tryAcquireLock();
            long now = System.currentTimeMillis();
            for (Map.Entry<String, Job> entry : mJobsSet.entrySet()) {
                final Job job = entry.getValue();
                if (job.info().mHappen == -1L && (job.info().mDeadline > 0 && job.info().mDeadline <= now)) { //not mHappen yet
                    trigger(job);
                }
            }
            tryReleaseLock();
        }

        /**
         * Checks the device is on.
         */
        private void checkDeviceOnImpl() {
            tryAcquireLock();
            final File dir = new File(getFilesDir(), JOB_PERSIST_DIR);
            if (dir.listFiles() == null)
                return;
            for (File f : dir.listFiles()) {
                if (!f.getName().startsWith("1553")) {
                    f.delete();
                    continue;
                }
                Job.JobInfo info = null;
                try {
                    info = Job.JobInfo.readFromFile(f);
                } catch (IOException ignore) {
                }
                if (info != null) {
                    Job job = Job.createJobFromPersistInfo(info);
                    if (job != null) {
                        job.info().mHappen = -1L;
                        addJob(job, true);
                    }
                } else {
                    f.delete();
                }
            }
            tryReleaseLock();
        }

        /**
         * Remove the persisted job if it was persisted.
         *
         * @param tag The tag.
         */
        private void removePersistJobWithTagImpl(String tag) {
            File backupDir = new File(getFilesDir(), JOB_BACKUP_DIR);
            File persistDir = new File(getFilesDir(), JOB_PERSIST_DIR);
            //Remove from backups
            if (backupDir.exists() && backupDir.listFiles() != null) {
                for (File file : backupDir.listFiles()) {
                    try {
                        Job.JobInfo info = Job.JobInfo.readFromFile(file);
                        if (info != null && info.mTag.equals(tag)) {
                            file.delete();
                        }
                    } catch (IOException ignore) {
                    }
                }
            }

            //Remove from persisted
            if (persistDir.exists() && persistDir.listFiles() != null) {
                for (File f : persistDir.listFiles()) {
                    try {
                        Job.JobInfo info = Job.JobInfo.readFromFile(f);
                        if (info != null && info.mTag.equals(tag)) {
                            f.delete();
                        }
                    } catch (IOException ignore) {
                    }
                }
            }
        }

        /**
         * Checks all the status of the conditions and ensures if this
         * should be triggered.
         *
         * @param job The job to trigger.
         * @return True if it was triggered, false otherwise.
         */
        private boolean mayTriggerAfterCheck(@NonNull final Job job) {
            //TODO send message with trigger
            for (String key : job.conditionsOk().keySet()) {
                if (!job.conditionsOk().get(key)) {
                    return false;
                }
            }
            trigger(job);
            return true;
        }

        /**
         * Executes the job provided.
         *
         * @param job The job provided.
         */
        private void trigger(@NonNull final Job job) {
            if (job.info().mHappen != -1 && job.info().mDelay != -1) {
                //Do not trigger, it is delayed
                if (SystemClock.elapsedRealtime() - job.info().mHappen < job.info().mDelay) {
                    return;
                }
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Act act = job.action();
                    if (act instanceof Schedule) {
                        ((Schedule) act).execute();
                    } else if (act instanceof ScheduleContext) {
                        ((ScheduleContext) act).act(HaloSchedulerService.this);
                    }
                }
            };
            mThreadManager.enqueue(job.info().mThreadPolicy, runnable);
            job.info().mHappen = SystemClock.elapsedRealtime();
            removeOne(job.info().mIdentity);
            job.resetConds();
            if (job.info().mRepeat) {
                addJob(job, false);
            }
        }

        /**
         * Checks if the given trigger is satisfied.
         *
         * @param cond The trigger to check.
         */
        public void checkSatisfy(TriggerDesc cond) {
            obtainMessage(MSG_TRIGGER_SATISFY, cond).sendToTarget();
        }

        /**
         * Sends an empy message
         */
        public void checkDeadline() {
            sendEmptyMessage(MSG_DEADLINE);
        }

        /**
         * Send a message to see if the device is on.
         */
        public void checkDeviceBootCompleted() {
            sendEmptyMessage(MSG_BOOT_DEVICE_COMPLETED);
        }

        /**
         * Check the status change based on the source.
         *
         * @param which The source of the status change.
         */
        public void checkStatusChanged(@NonNull String which) {
            obtainMessage(MSG_STATUS_CHANGED, which).sendToTarget();
        }

        /**
         * Remove a persisted job based on a tag.
         *
         * @param tag The tag.
         */
        public void removePersistJobWithTag(String tag) {
            obtainMessage(MSG_REMOVE_TAG_JOB, tag).sendToTarget();
        }

        /**
         * Cleanups all the pending messages.
         */
        public void cleanup() {
            removeCallbacksAndMessages(null);
        }
    }

    /**
     * Deadline broadcast checker if a job reaches the deadline.
     */
    private class DeadlineCheck extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mChecker.checkDeadline();
        }
    }

    /**
     * Inner receiver.
     */
    private class ReceiverInner extends BroadcastReceiver {
        /**
         * The receiver for the inner events.
         */
        private final Receiver mReceiver;

        /**
         * The receiver constructor that wraps another receiver.
         *
         * @param receiver The receiver.
         */
        private ReceiverInner(@NonNull Receiver receiver) {
            mReceiver = receiver;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mReceiver.onReceive(context, intent);
        }
    }
}
