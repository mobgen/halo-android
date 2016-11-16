package com.mobgen.halo.android.sdk.api;

import com.mobgen.halo.android.sdk.core.internal.startup.ReadyChecker;
import com.mobgen.halo.android.sdk.core.internal.startup.StartupManager;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;
import static com.mobgen.halo.android.sdk.mock.instrumentation.StartupManagerInstrument.givenAReadyListenerFlagged;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class HaloTest extends HaloRobolectricTest {

    private CallbackFlag mCallbackFlag;

    @Before
    public void initialize() {
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() {
        Halo.instance().uninstall();
    }

    @Test
    public void thatIntegrateInstallationListener() throws InterruptedException {
        HaloMock.create().ready(givenAReadyListenerFlagged(mCallbackFlag));
        TestUtils.flushMainThread();
        assertThat(mCallbackFlag.timesExecuted()).isGreaterThan(0);
    }


}
