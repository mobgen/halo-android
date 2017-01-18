package com.mobgen.halo.android.sdk.core.internal.startup.processes;


import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.TagCollector;
import com.mobgen.halo.android.sdk.core.management.segmentation.TestDeviceCollector;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.SYNC_DEVICE;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.sdk.mock.instrumentation.StartupManagerInstrument.givenAProcessListener;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class DeviceSyncStartupProcessTest extends HaloRobolectricTest {

    private Halo mHalo;
    private CallbackFlag mCallbackFlag;
    private MockServer mMockServer;

    @Before
    public void initialize() throws IOException {
        List<TagCollector> collectors = new ArrayList<>();
        collectors.add(new TestDeviceCollector(true));
        mMockServer = MockServer.create();
        mHalo = HaloMock.create(mMockServer.start(), collectors);
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanSyncDevice() throws IOException {
        enqueueServerFile(mMockServer, SYNC_DEVICE);
        final Device device = new Device();
        SyncDeviceStartupProcess syncDeviceStartupProcess = new SyncDeviceStartupProcess();
        syncDeviceStartupProcess.setProcessListener(givenAProcessListener(mCallbackFlag));
        StartupRunnableAdapter startupRunnableAdapter = new StartupRunnableAdapter(mHalo, syncDeviceStartupProcess);
        startupRunnableAdapter.run();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mHalo.manager().getDevice()).isNotEqualTo(device);
        assertThat(mHalo.manager().getDevice().getTags().size()).isEqualTo(2);
    }

    @Test
    public void thatCanSyncDeviceError() throws IOException {
        enqueueServerFile(mMockServer, SYNC_DEVICE);
        final Device device = null;
        SyncDeviceStartupProcess syncDeviceStartupProcess = new SyncDeviceStartupProcess();
        syncDeviceStartupProcess.setProcessListener(givenAProcessListener(mCallbackFlag));
        StartupRunnableAdapter startupRunnableAdapter = new StartupRunnableAdapter(null, syncDeviceStartupProcess);
        startupRunnableAdapter.run();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mHalo.manager().getDevice()).isEqualTo(device);
    }

    @Test
    public void thatThreadPolicyIsCorrect() {
        assertThat(new SyncDeviceStartupProcess().getThreadPolicy()).isEqualTo(Threading.POOL_QUEUE_POLICY);
    }
}
