package com.mobgen.halo.android.sdk.core.internal.startup.processes;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;

/**
 * Checks the version and logs a warning in case the version does not match the
 * needed requirements.
 */
public class VersionCheckStartupProcess extends StartupProcess {

    @Override
    public int getThreadPolicy() {
        return Threading.POOL_QUEUE_POLICY;
    }

    @Override
    protected void onStart(@NonNull final Halo halo) {
        halo.getCore().manager().getServerVersion()
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .bypassHaloReadyCheck()
                .execute(new CallbackV2<HaloServerVersion>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<HaloServerVersion> result) {
                        HaloServerVersion version = result.data();
                        halo.getCore().serverVersionCheck(version);
                    }
                });
    }
}
