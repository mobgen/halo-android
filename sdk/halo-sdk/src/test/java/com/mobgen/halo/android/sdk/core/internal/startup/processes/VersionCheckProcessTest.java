package com.mobgen.halo.android.sdk.core.internal.startup.processes;

import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;
import com.mobgen.halo.android.sdk.mock.HaloMock;

import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.TEST_SERVER_VERSION;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.GET_VALID_SERVER_VERSION;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.GET_OUTDATED_SERVER_VERSION;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.sdk.mock.instrumentation.StartupManagerInstrument.givenAProcessListener;

import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class VersionCheckProcessTest extends HaloRobolectricTest {

    private Halo mHalo;
    private CallbackFlag mCallbackFlag;
    private MockServer mMockServer;


    @Before
    public void initialize() throws IOException {
        mMockServer = MockServer.create();
        mHalo = HaloMock.create(mMockServer.start());
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() throws IOException{
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatVersionStartUpProcessEnd() throws IOException {
        enqueueServerFile(mMockServer, TEST_SERVER_VERSION);
        VersionCheckStartupProcess versionCheckStartupProcess = new VersionCheckStartupProcess();
        versionCheckStartupProcess.setProcessListener(givenAProcessListener(mCallbackFlag));
        StartupRunnableAdapter startupRunnableAdapter = new StartupRunnableAdapter(mHalo,versionCheckStartupProcess);
        startupRunnableAdapter.run();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mHalo.getCore().isVersionValid()).isTrue();
    }

    @Test
    public void thatVersionIsNotChecked() throws IOException{
        enqueueServerFile(mMockServer, TEST_SERVER_VERSION);
        VersionCheckStartupProcess versionCheckStartupProcess = new VersionCheckStartupProcess();
        versionCheckStartupProcess.setProcessListener(givenAProcessListener(mCallbackFlag));
        StartupRunnableAdapter startupRunnableAdapter = new StartupRunnableAdapter(mHalo,versionCheckStartupProcess);
        startupRunnableAdapter.run();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mHalo.getCore().serverVersionCheck()).isEqualTo(HaloServerVersion.NOT_CHECKED);
        assertThat(mHalo.getCore().isVersionValid()).isTrue();
    }

    @Test
    public void thatVersionIsValid() throws IOException {
        enqueueServerFile(mMockServer, GET_VALID_SERVER_VERSION);
        VersionCheckStartupProcess versionCheckStartupProcess = new VersionCheckStartupProcess();
        versionCheckStartupProcess.setProcessListener(givenAProcessListener(mCallbackFlag));
        StartupRunnableAdapter startupRunnableAdapter = new StartupRunnableAdapter(mHalo,versionCheckStartupProcess);
        startupRunnableAdapter.run();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mHalo.getCore().serverVersionCheck()).isEqualTo(HaloServerVersion.VALID);
        assertThat(mHalo.getCore().isVersionValid()).isTrue();
    }

    @Test
    public void thatVersionIsOutdated() throws IOException {
        enqueueServerFile(mMockServer,GET_OUTDATED_SERVER_VERSION);
        VersionCheckStartupProcess versionCheckStartupProcess = new VersionCheckStartupProcess();
        versionCheckStartupProcess.setProcessListener(givenAProcessListener(mCallbackFlag));
        StartupRunnableAdapter startupRunnableAdapter = new StartupRunnableAdapter(mHalo,versionCheckStartupProcess);
        startupRunnableAdapter.run();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mHalo.getCore().serverVersionCheck()).isEqualTo(HaloServerVersion.OUTDATED);
        assertThat(mHalo.getCore().isVersionValid()).isFalse();
    }

    @Test
    public void thatThreadPolicyIsCorrect() {
        assertThat(new VersionCheckStartupProcess().getThreadPolicy()).isEqualTo(Threading.POOL_QUEUE_POLICY);
    }
}
