package com.mobgen.halo.android.content.sync;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.mock.dummy.DummyItem;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.ICancellable;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.SYNC_CREATE_MODULE;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.SYNC_UPDATE_MODULE;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.SYNC_UP_TO_DATE;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloContentApiMock.givenAContentApi;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenAOkClientWithCustomInterceptor;
import static com.mobgen.halo.android.content.mock.instrumentation.SyncInstruments.givenACallbackThatCheckNewModuleInstances;
import static com.mobgen.halo.android.content.mock.instrumentation.SyncInstruments.givenACallbackThatCheckParsedInstances;
import static com.mobgen.halo.android.content.mock.instrumentation.SyncInstruments.givenACallbackThatChecksLogs;
import static com.mobgen.halo.android.content.mock.instrumentation.SyncInstruments.givenACallbackWithEmptyData;
import static com.mobgen.halo.android.content.mock.instrumentation.SyncInstruments.givenASyncListener;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloContentSyncTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private Halo mHalo;
    private HaloContentApi mHaloContentApi;
    private CallbackFlag mCallbackFlag;
    private static final String MODULE_NAME_FAKE = "moduleName";
    private static final SyncQuery QUERY = SyncQuery.create(MODULE_NAME_FAKE, Threading.SAME_THREAD_POLICY);

    @Override
    public void onStart() throws Exception {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        mHaloContentApi = givenAContentApi(mHalo);
        mCallbackFlag = newCallbackFlag();
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatAFirstSyncLifecycleCompletes() throws IOException {
        enqueueServerFile(mMockServer, SYNC_CREATE_MODULE);
        enqueueServerFile(mMockServer, SYNC_UP_TO_DATE);
        HaloContentApi.HaloSyncListener syncListener = givenASyncListener(mCallbackFlag);
        CallbackV2<List<HaloContentInstance>> callbackInstances = givenACallbackThatCheckNewModuleInstances(mCallbackFlag);

        //Subscribe
        ISubscription syncSubscription = mHaloContentApi.subscribeToSync(MODULE_NAME_FAKE, syncListener);

        //Perform the sync
        mHaloContentApi.sync(QUERY, false);

        //Check the instances
        ICancellable cancellable = mHaloContentApi.getSyncInstances(MODULE_NAME_FAKE)
                .asContent()
                .execute(callbackInstances);

        syncSubscription.unsubscribe();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatASyncCanBeParsedToCustomObjects() throws IOException {
        enqueueServerFile(mMockServer, SYNC_CREATE_MODULE);
        enqueueServerFile(mMockServer, SYNC_UP_TO_DATE);
        CallbackV2<List<DummyItem>> callbackInstances = givenACallbackThatCheckParsedInstances(mCallbackFlag, "bar", "bar", "bar");

        mHaloContentApi.sync(QUERY, false);

        ICancellable cancellable = mHaloContentApi.getSyncInstances(MODULE_NAME_FAKE)
                .asContent(DummyItem.class)
                .execute(callbackInstances);

        assertThat(mCallbackFlag.isFlagged());
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatAnUpdatedSyncRefreshesDataInformation() throws IOException {
        enqueueServerFile(mMockServer, SYNC_CREATE_MODULE);
        enqueueServerFile(mMockServer, SYNC_UP_TO_DATE);
        enqueueServerFile(mMockServer, SYNC_UPDATE_MODULE);
        CallbackV2<List<DummyItem>> callbackInstances = givenACallbackThatCheckParsedInstances(mCallbackFlag, "bar", "bar updated");

        //First sync
        mHaloContentApi.sync(QUERY, false);
        //Second sync
        mHaloContentApi.sync(QUERY, false);

        ICancellable cancellable = mHaloContentApi.getSyncInstances(MODULE_NAME_FAKE)
                .asContent(DummyItem.class)
                .execute(callbackInstances);

        assertThat(mCallbackFlag.isFlagged());
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatClearingInstancesProvidesEmptyResponse() throws IOException {
        enqueueServerFile(mMockServer, SYNC_CREATE_MODULE);
        enqueueServerFile(mMockServer, SYNC_UP_TO_DATE);
        CallbackV2<List<HaloContentInstance>> callback = givenACallbackWithEmptyData(mCallbackFlag);

        mHaloContentApi.sync(QUERY, false);
        mHaloContentApi.clearSyncInstances(MODULE_NAME_FAKE)
                .execute(null);
        mHaloContentApi.getSyncInstances(MODULE_NAME_FAKE)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
    }

    @Test
    public void thatALogIsCreatedPerSync() throws IOException {
        enqueueServerFile(mMockServer, SYNC_CREATE_MODULE);
        enqueueServerFile(mMockServer, SYNC_UP_TO_DATE);
        enqueueServerFile(mMockServer, SYNC_UPDATE_MODULE);
        CallbackV2<List<HaloSyncLog>> callback = givenACallbackThatChecksLogs(mCallbackFlag, 2);

        //First sync
        mHaloContentApi.sync(QUERY, false);
        //Second sync
        mHaloContentApi.sync(QUERY, false);

        mHaloContentApi.getSyncLog(MODULE_NAME_FAKE)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
    }

    @Test
    public void thatCanSyncWithDiffertentServerCache() throws IOException {
        enqueueServerFile(mMockServer, SYNC_CREATE_MODULE);
        SyncQuery QUERY_CACHE = SyncQuery.create(MODULE_NAME_FAKE, Threading.SAME_THREAD_POLICY, 15);
        mHalo.framework().network().client().overrideOk(givenAOkClientWithCustomInterceptor(mHalo.framework().network().client(), "15"));
        mHaloContentApi.sync(QUERY_CACHE, true);
    }
}
