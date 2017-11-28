package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;

import java.util.HashMap;
import java.util.Map;

/**
 * HaloJobScheduler. A window let you schedule, cancel jobs.
 */
public class HaloJobScheduler {
    /**
     * Tag for the scheduler.
     */
    private static final String TAG = "HaloJobScheduler";
    /**
     * Testing case for the scheduler tests.
     */
    static final String DEBUG_DEVICE_ON_B = "trigger.testcase.deviceon";
    /**
     * The debug state.
     */
    static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    /**
     * Binder for the service.
     */
    private IHaloSchedulerServiceBinder mServiceBinder;
    /**
     * The application context.
     */
    private Context mContext;
    /**
     * Pending jobs.
     */
    private HashMap<String, Job> mPendingList;
    /**
     * Pending stop and reset before bind.
     */
    private boolean mStopAndResetPending;
    /**
     * The service connector.
     */
    private Connector mConnector;
    /**
     * The thread manager.
     */
    private HaloThreadManager mThreadManager;

    /**
     * Creates a job scheduler using the context.
     *
     * @param context      The context.
     * @param threadManger The thread manager.
     */
    public HaloJobScheduler(@NonNull Context context, @NonNull HaloThreadManager threadManger) {
        mPendingList = new HashMap<>();
        mConnector = new Connector();
        mStopAndResetPending = false;
        mContext = context.getApplicationContext();
        mThreadManager = threadManger;
        mContext.bindService(HaloSchedulerService.newIntent(mContext), mConnector, Context.BIND_AUTO_CREATE);
    }

    /**
     * Schedule a job.
     *
     * @param job {@link Job} with action and maybe one or more extra conditions.
     */
    @Api(2.0)
    public void schedule(@NonNull Job job) {
        AssertionUtils.notNull(job, "job");
        if (mServiceBinder == null) {
            mPendingList.put(job.info().mIdentity, job);
        } else {
            mServiceBinder.schedule(job);
        }
    }

    /**
     * Schedule multi job version
     *
     * @param jobs {@link Job} with mAction and maybe one or more extra conditions.
     */
    @Api(2.0)
    public void schedule(@NonNull Job... jobs) {
        AssertionUtils.notNull(jobs, "jobs");
        for (Job j : jobs) {
            schedule(j);
        }
    }

    /**
     * Cancel a job with given tag, if the job is in the pending list, it can be cancelled.
     * If the job's mAction is in progress, this can not help you.
     *
     * @param tag Given tag.
     */
    @Api(2.0)
    public void cancel(@NonNull String tag) {
        AssertionUtils.notNull(tag, "tag");
        if (mServiceBinder == null && mPendingList.containsKey(tag)) {
            mPendingList.remove(tag);
        } else {
            mServiceBinder.cancel(tag);
        }
    }

    /**
     * Don't accept any Job from then. Not effect the jobs already in pending list.
     */
    @Api(2.0)
    public void closeDoor() {
        mServiceBinder = null;
        mContext.unbindService(mConnector);
    }

    /**
     * Reset all things, include cancel jobs in pending list and clear all jobs status,
     * after this, HaloJobScheduler can accept new job all the same.
     */
    @Api(2.0)
    public void stopAndReset() {
        if (mServiceBinder != null) {
            mServiceBinder.stopAndReset();
        } else {
            mStopAndResetPending = true;
        }
    }

    /**
     * Class for the connection between the service and the scheduler.
     */
    private class Connector implements ServiceConnection {

        @Override
        public void onServiceConnected(@NonNull ComponentName name, @NonNull IBinder service) {
            mServiceBinder = IHaloSchedulerServiceBinder.Stub.asInterface(service);
            mServiceBinder.threadManager(mThreadManager);
            if (mStopAndResetPending) {
                mServiceBinder.stopAndReset();
                mStopAndResetPending = false;
            }
            if (mPendingList != null) {
                for (Map.Entry<String, Job> entry : mPendingList.entrySet()) {
                    Job job = entry.getValue();
                    mServiceBinder.schedule(job);
                }
                mPendingList.clear();
            }
        }

        @Override
        public void onServiceDisconnected(@NonNull ComponentName name) {

        }
    }
}
