package com.mobgen.halo.android.sdk.core.threading;


import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.scheduler.Schedule;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Schedule that uses all the internal items from Halo to avoid being executed when it is not needed.
 */
@Keep
public abstract class HaloSchedule extends Schedule {

    /**
     * Needs the halo instance to be executed.
     */
    protected Halo mHalo;

    /**
     * The halo to check the ready params.
     *
     * @param halo The halo instance.
     */
    public HaloSchedule(@NonNull Halo halo) {
        AssertionUtils.notNull(halo, "halo");
        mHalo = halo;
    }

    @Override
    protected final void execute() {
        new HaloInteractorExecutor<>(
                mHalo,
                "Executing a schedule",
                new ScheduleInteractor())
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute();
    }

    /**
     * Executes this method when halo is ready to do so.
     */
    public abstract void executeWhenReady();

    /**
     * The schedule interactor.
     */
    private class ScheduleInteractor implements HaloInteractorExecutor.Interactor<Void> {

        @NonNull
        @Override
        public HaloResultV2<Void> executeInteractor() throws Exception {
            executeWhenReady();
            return new HaloResultV2<>(HaloStatus.builder().build(), null);
        }
    }
}
