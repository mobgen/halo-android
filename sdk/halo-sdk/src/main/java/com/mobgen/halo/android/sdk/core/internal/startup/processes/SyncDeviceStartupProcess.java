package com.mobgen.halo.android.sdk.core.internal.startup.processes;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Syncs the device on startup.
 */
public class SyncDeviceStartupProcess extends StartupProcess {

    @Override
    public int getThreadPolicy() {
        return Threading.POOL_QUEUE_POLICY;
    }

    @Override
    protected void onStart(@NonNull Halo halo) {
        halo.getCore().manager().syncDevice().bypassHaloReadyCheck().threadPolicy(Threading.SAME_THREAD_POLICY).execute();
    }
}
