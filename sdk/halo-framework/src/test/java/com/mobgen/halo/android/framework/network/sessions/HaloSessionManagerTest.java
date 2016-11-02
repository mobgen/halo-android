package com.mobgen.halo.android.framework.network.sessions;

import com.mobgen.halo.android.framework.mock.instrumentation.HaloSessionInstrument;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloSessionManagerTest extends HaloRobolectricTest {

    @Before
    public void initialize() throws IOException {
    }

    @After
    public void tearDown() throws IOException {
    }

    @Test
    public void thatCanCreateANewSessionOnSessionMagener(){
        HaloSessionManager haloSessionManager = new HaloSessionManager();
        HaloSessionInstrument haloSessionInstrument = new HaloSessionInstrument();
        haloSessionManager.setSession("myNewSession",haloSessionInstrument);
        assertThat(haloSessionManager.getSession("myNewSession")).isEqualTo(haloSessionInstrument);
    }

    @Test
    public void thatCanRemoveASessionFromSessionMagener(){
        HaloSessionManager haloSessionManager = new HaloSessionManager();
        HaloSessionInstrument haloSessionInstrument = new HaloSessionInstrument();
        haloSessionManager.setSession("myNewSession",haloSessionInstrument);
        haloSessionManager.flushSession("myNewSession");
        assertThat(haloSessionManager.getSession("myNewSession")).isNull();
    }

}
