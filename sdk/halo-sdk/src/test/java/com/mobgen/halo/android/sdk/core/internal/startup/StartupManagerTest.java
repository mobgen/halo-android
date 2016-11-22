package com.mobgen.halo.android.sdk.core.internal.startup;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloInstallationListener;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupProcess;
import static com.mobgen.halo.android.sdk.mock.instrumentation.StartupManagerInstrument.givenAProcess;
import static com.mobgen.halo.android.sdk.mock.instrumentation.StartupManagerInstrument.givenAInstallationListener;
import static com.mobgen.halo.android.sdk.mock.instrumentation.StartupManagerInstrument.givenAReadyListener;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class StartupManagerTest extends HaloRobolectricTest {

    private StartupManager mManager;
    private Halo mHalo;
    private ReadyChecker mReadyChecker;
    private CallbackFlag mCallbackFlag;

    @Before
    public void initialize() {
        mCallbackFlag = newCallbackFlag();
        mHalo = HaloMock.create();
        mManager = new StartupManager(mHalo);
        mReadyChecker = new ReadyChecker(mManager);
    }

    @After
    public void tearDown() {
        mHalo.uninstall();
    }

    @Test
    public void thatStartupEndsWithATask() {
        StartupProcess process = givenAProcess(mCallbackFlag);
        HaloInstallationListener listener = givenAInstallationListener(mCallbackFlag,false);
        mManager.setInstallationListener(listener);
        mManager.execute(process);
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        assertThat(mManager.isRunning()).isFalse();
        assertThat(mManager.hasFinished()).isTrue();

    }

    @Test
    public void thatStartupEndsWithMultipleTasks() {
        StartupProcess processStartup1 = givenAProcess(mCallbackFlag);
        StartupProcess processStartup2 = givenAProcess(mCallbackFlag);
        HaloInstallationListener listener = givenAInstallationListener(mCallbackFlag,false);
        StartupProcess[] arrayOfProcess  = new StartupProcess[]{processStartup1,processStartup2};
        mManager.setInstallationListener(listener);
        mManager.execute(arrayOfProcess);
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        assertThat(mManager.isRunning()).isFalse();
        assertThat(mManager.hasFinished()).isTrue();

    }

    @Test
    public void thatStartupEndsWithoutATask() {
        HaloInstallationListener listener = givenAInstallationListener(mCallbackFlag,true);
        mManager.setInstallationListener(listener);
        mManager.execute();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(0);
        assertThat(mManager.isRunning()).isFalse();
        assertThat(mManager.hasFinished()).isTrue();
    }

    @Test
    public void thatReadyNotify() throws InterruptedException{
        StartupProcess process = givenAProcess(mCallbackFlag);
        mManager.execute(process);
        HaloReadyListener haloReadyListener = givenAReadyListener(mCallbackFlag);
        mHalo.ready(haloReadyListener);
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        assertThat(mManager.hasFinished()).isTrue();
    }

    @Test
    public void thatReadyNotifyAfterQueueATask() throws InterruptedException{
        StartupProcess process = givenAProcess(mCallbackFlag);
        HaloReadyListener haloReadyListener = givenAReadyListener(mCallbackFlag);
        mReadyChecker.checkReady(haloReadyListener);
        mManager.execute(process);
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        assertThat(mManager.hasFinished()).isTrue();
    }

}
