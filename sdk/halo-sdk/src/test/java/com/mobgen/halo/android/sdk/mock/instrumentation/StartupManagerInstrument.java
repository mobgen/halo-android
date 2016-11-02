package com.mobgen.halo.android.sdk.mock.instrumentation;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloInstallationListener;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.ProcessListener;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupProcess;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;
import com.mobgen.halo.android.testing.CallbackFlag;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class StartupManagerInstrument {

    public static StartupProcess givenAProcess(final CallbackFlag flag){
        return new StartupProcess() {
            @Override
            public int getThreadPolicy() {
                return Threading.POOL_QUEUE_POLICY;
            }

            @Override
            protected void onStart(@NonNull Halo halo) {
                flag.flagExecuted();
            }
        };
    }

    public static ProcessListener givenAProcessListener(final CallbackFlag flag){
        return new ProcessListener() {
            @Override
            public void onProcessFinished() {
                flag.flagExecuted();
            }
        };
    }

    public static HaloInstallationListener givenAInstallationListener(final CallbackFlag flag, final Boolean isEmptyTask) {
        return new HaloInstallationListener() {
            @Override
            public void onFinishedInstallation() {
                if(isEmptyTask){
                    assertThat(flag.timesExecuted()).isEqualTo(0);
                }
                else {
                    assertThat(flag.timesExecuted()).isGreaterThan(0);
                }
            }
        };
    }

    public static HaloReadyListener givenAReadyListener(final CallbackFlag flag){
        return new HaloReadyListener() {
            @Override
            public void onHaloReady() {
                assertThat(flag.timesExecuted()).isGreaterThan(0);
            }
        };
    }

    public static HaloReadyListener givenAReadyListenerFlagged(final CallbackFlag flag){
        return new HaloReadyListener() {
            @Override
            public void onHaloReady() {
                flag.flagExecuted();
            }
        };
    }
}
