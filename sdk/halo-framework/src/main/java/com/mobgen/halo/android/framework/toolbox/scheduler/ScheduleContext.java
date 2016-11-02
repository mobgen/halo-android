package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.Context;

/**
 * Compare to {@link Schedule}, ContextAction bring a context here. The context come from {@link HaloSchedulerService}
 */
public abstract class ScheduleContext implements Act {
    /**
     * Perform an action using the context.
     *
     * @param context The service context.
     */
    protected abstract void act(Context context);
}
