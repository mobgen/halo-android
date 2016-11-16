package com.mobgen.halo.android.framework.api;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.bus.EventBus;
import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;

/**
 * Synchronization API that allows the creation of @{link HaloNetworkAction} items linking them
 * to its related String id.
 */
public class HaloToolboxApi {

    /**
     * The request dispatcher.
     */
    private final HaloThreadManager mQueue;

    /**
     * The event hub to emit events.
     */
    private EventBus mEventHub;

    /**
     * The job scheduler.
     */
    private HaloJobScheduler mJobScheduler;

    /**
     * Instance of the halo framework that contains the toolbox api.
     */
    private final HaloFramework mFramework;

    /**
     * Constructs the synchronization api.
     *
     * @param framework    The framework instance.
     * @param syncQueue    The synchronization threadPolicy.
     * @param eventHub     The event hub.
     * @param jobScheduler The job scheduler for the toolbox.
     */
    protected HaloToolboxApi(@NonNull HaloFramework framework, @NonNull HaloThreadManager syncQueue, @NonNull EventBus eventHub, @NonNull HaloJobScheduler jobScheduler) {
        AssertionUtils.notNull(syncQueue, "toolbox queue");
        mQueue = syncQueue;
        mEventHub = eventHub;
        mJobScheduler = jobScheduler;
        mFramework = framework;
    }

    /**
     * Creates a new synchronization api.
     *
     * @param framework     The framework to create the api.
     * @param configuration The configuration.
     * @return The created toolbox api.
     */
    @Api(1.3)
    @NonNull
    public static HaloToolboxApi newSyncApi(@NonNull HaloFramework framework, @NonNull HaloConfig configuration) {
        return new HaloToolboxApi(framework, configuration.getSyncQueue(), configuration.getEventHub(), configuration.jobScheduler());
    }

    /**
     * Provides the request dispatcher.
     *
     * @return The request dispatcher.
     */
    @Api(1.1)
    @NonNull
    public HaloThreadManager queue() {
        return mQueue;
    }

    /**
     * Provides the halo event hub.
     *
     * @return The event hub.
     */
    @Api(1.3)
    @NonNull
    public EventBus eventHub() {
        return mEventHub;
    }

    /**
     * The task to schedule.
     *
     * @param job The task to schedule.
     */
    @Api(2.0)
    public void schedule(@NonNull Job job) {
        AssertionUtils.notNull(job, "job");
        mJobScheduler.schedule(job);
    }

    /**
     * Schedules multiple tasks at the same time.
     *
     * @param jobs The tasks to schedule.
     */
    @Api(2.0)
    public void schedule(@NonNull Job... jobs) {
        AssertionUtils.notNull(jobs, "jobs");
        mJobScheduler.schedule(jobs);
    }

    /**
     * Provides the framework instance;
     *
     * @return The framework instance.
     */
    @Api(1.3)
    @NonNull
    public HaloFramework framework() {
        return mFramework;
    }
}
