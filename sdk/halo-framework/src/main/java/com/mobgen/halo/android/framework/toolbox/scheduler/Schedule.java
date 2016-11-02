package com.mobgen.halo.android.framework.toolbox.scheduler;

/**
 * Normal action without context, if you want this,
 * you should see {@link ScheduleContext}
 */
public abstract class Schedule implements Act {
    /**
     * The mAction that does not require a context.
     */
    public Schedule() {
    }

    /**
     * Act callback for the action.
     */
    protected abstract void execute();
}
