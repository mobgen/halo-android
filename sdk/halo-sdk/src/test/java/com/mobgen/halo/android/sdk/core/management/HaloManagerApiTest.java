package com.mobgen.halo.android.sdk.core.management;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.device.DeviceLocalDatasource;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;
import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.sdk.core.threading.ICancellable;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.ADD_SEGMENTATION_TAG;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.GET_DEVICE;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.GET_MODULES;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.GET_MODULES_META_DATA;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.REMOVE_SEGMENTATION_TAG;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.REQUEST_TOKEN;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.SYNC_DEVICE;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.TEST_SERVER_VERSION;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackServerVersion;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithDeviceSegmentationTagAdd;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithDeviceSegmentationTagAddList;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithDeviceSegmentationTagRemoved;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithDeviceSegmentationTagRemovedList;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithGetDevice;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithGetDeviceAnonymous;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithGetModules;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithGetModulesAsRaw;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithRequestToken;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithSendDevice;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithSetNotificationToken;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenCallbackWithSyncDevice;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloManagerApiTest extends HaloRobolectricTest {

    private Halo mHalo;
    private MockServer mMockServer;
    private CallbackFlag mCallbackFlag;
    private ISubscription mDeviceSyncSubscription;

    @Before
    public void initialize() throws IOException {
        mMockServer = MockServer.create();
        mHalo = HaloMock.create(mMockServer.start());
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatAddADeviceTag() throws IOException {
        enqueueServerFile(mMockServer, ADD_SEGMENTATION_TAG);
        final HaloSegmentationTag mySegmentationTag = new HaloSegmentationTag("myCustomTagYear", "1984");
        CallbackV2<Device> callback = givenCallbackWithDeviceSegmentationTagAdd(mCallbackFlag, mySegmentationTag);
        ICancellable cancellable = mHalo.getCore().manager()
                .addDeviceTag(mySegmentationTag, true)
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatAddADeviceTagList() throws IOException {
        enqueueServerFile(mMockServer, ADD_SEGMENTATION_TAG);
        final HaloSegmentationTag mySegmentationTagYear = new HaloSegmentationTag("myCustomTagYear", "1984");
        final HaloSegmentationTag mySegmentationTagMonth = new HaloSegmentationTag("myCustomTagMonth", "12");
        List<HaloSegmentationTag> mySegmentationTagList = new ArrayList<>(Arrays.asList(mySegmentationTagYear, mySegmentationTagMonth));
        CallbackV2<Device> callback = givenCallbackWithDeviceSegmentationTagAddList(mCallbackFlag, mySegmentationTagList);
        ICancellable cancellable = mHalo.getCore().manager()
                .addDeviceTags(mySegmentationTagList, true)
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatRemoveADeviceTag() throws IOException {
        enqueueServerFile(mMockServer, REMOVE_SEGMENTATION_TAG);
        final HaloSegmentationTag mySegmentationTag = new HaloSegmentationTag("myCustomTagYear", "1984");
        CallbackV2<Device> callback = givenCallbackWithDeviceSegmentationTagRemoved(mCallbackFlag, mySegmentationTag);
        ICancellable cancellable = mHalo.getCore().manager()
                .removeDeviceTag("myCustomTagYear", true)
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatRemoveADeviceTagList() throws IOException {
        enqueueServerFile(mMockServer, REMOVE_SEGMENTATION_TAG);
        List<String> removeTagListNames = new ArrayList<>(Arrays.asList("myCustomTagYear", "myCustomTagMonth"));
        final HaloSegmentationTag mySegmentationTagYear = new HaloSegmentationTag("myCustomTagYear", "1984");
        final HaloSegmentationTag mySegmentationTagMonth = new HaloSegmentationTag("myCustomTagMonth", "12");
        List<HaloSegmentationTag> mySegmentationTagList = new ArrayList<>(Arrays.asList(mySegmentationTagYear, mySegmentationTagMonth));
        CallbackV2<Device> callback = givenCallbackWithDeviceSegmentationTagRemovedList(mCallbackFlag, mySegmentationTagList);
        ICancellable cancellable = mHalo.getCore().manager()
                .removeDeviceTags(removeTagListNames, true)
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesAsContentNetwork() throws IOException {
        enqueueServerFile(mMockServer, GET_MODULES);
        CallbackV2<List<HaloModule>> callback = givenCallbackWithGetModules(mCallbackFlag, true);
        ICancellable cancellable = mHalo.getCore().manager()
                .getModules(Data.NETWORK_ONLY)
                .asContent()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesNetworkAndStorage() throws IOException {
        enqueueServerFile(mMockServer, GET_MODULES);
        CallbackV2<List<HaloModule>> callback = givenCallbackWithGetModules(mCallbackFlag, true);
        ICancellable cancellable = mHalo.getCore().manager()
                .getModules(Data.NETWORK_AND_STORAGE)
                .asContent()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesStorage() throws IOException {
        CallbackV2<List<HaloModule>> callback = givenCallbackWithGetModules(mCallbackFlag, false);
        ICancellable cancellable = mHalo.getCore().manager()
                .getModules(Data.STORAGE_ONLY)
                .asContent()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesAsRawNetwork() throws IOException {
        enqueueServerFile(mMockServer, GET_MODULES);
        CallbackV2<Cursor> callback = givenCallbackWithGetModulesAsRaw(mCallbackFlag, true);
        ICancellable cancellable = mHalo.getCore().manager()
                .getModules(Data.NETWORK_AND_STORAGE)
                .asRaw()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesAsRawNetworkAndStorage() throws IOException {
        enqueueServerFile(mMockServer, GET_MODULES);
        CallbackV2<Cursor> callback = givenCallbackWithGetModulesAsRaw(mCallbackFlag, true);
        ICancellable cancellable = mHalo.getCore().manager()
                .getModules(Data.NETWORK_AND_STORAGE)
                .asRaw()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesAsRawStorage() throws IOException {
        CallbackV2<Cursor> callback = givenCallbackWithGetModulesAsRaw(mCallbackFlag, false);
        ICancellable cancellable = mHalo.getCore().manager()
                .getModules(Data.STORAGE_ONLY)
                .asRaw()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetModulesWithMetadata() throws IOException {
        enqueueServerFile(mMockServer, GET_MODULES_META_DATA);
        ICancellable cancellable = mHalo.getCore().manager().printModulesMetaData();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatCurrentVersionServerChange() throws IOException {
        enqueueServerFile(mMockServer, TEST_SERVER_VERSION);
        CallbackV2<HaloServerVersion> callback = givenCallbackServerVersion(mCallbackFlag, mHalo.getCore().version());
        ICancellable cancellable = mHalo.getCore().manager()
                .getServerVersion()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatGetCurrentDeviceAnonymous() throws IOException {
        enqueueServerFile(mMockServer, GET_DEVICE);
        String alias = new Device().getAlias();
        assertThat(alias).isNull();
        CallbackV2<Device> callback = givenCallbackWithGetDeviceAnonymous(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .fetchCurrentDevice()
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
        assertThat(mHalo.manager().getDevice().getAlias()).isNull();
    }

    @Test
    public void thatGetCurrentDevice() throws IOException, HaloParsingException {
        enqueueServerFile(mMockServer, GET_DEVICE);
        Device device = new Device("myTestUser", "57fb592ff53f3f002aa99d78", null, null);
        DeviceLocalDatasource deviceLocalDatasource = new DeviceLocalDatasource(mHalo.getCore().manager().storage());
        deviceLocalDatasource.clearCurrentDevice();
        deviceLocalDatasource.cacheDevice(Device.serialize(device, mHalo.getCore().framework().parser()));
        String alias = "myTestUser";
        CallbackV2<Device> callback = givenCallbackWithGetDevice(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .fetchCurrentDevice()
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
        assertThat(mHalo.getCore().manager().getDevice().getAlias()).isEqualTo(alias);
    }

    @Test
    public void thatSyncDeviceAnonymous() throws IOException {
        enqueueServerFile(mMockServer, SYNC_DEVICE);
        CallbackV2<Device> callback = givenCallbackWithSyncDevice(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .syncDevice()
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatSyncDevice() throws IOException, HaloParsingException {
        enqueueServerFile(mMockServer, GET_DEVICE);
        enqueueServerFile(mMockServer, SYNC_DEVICE);
        Device device = new Device("myTestUser", "57fb592ff53f3f002aa99d78", null, null);
        DeviceLocalDatasource deviceLocalDatasource = new DeviceLocalDatasource(mHalo.getCore().manager().storage());
        deviceLocalDatasource.clearCurrentDevice();
        deviceLocalDatasource.cacheDevice(Device.serialize(device, mHalo.getCore().framework().parser()));
        CallbackV2<Device> callback = givenCallbackWithSyncDevice(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .syncDevice()
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
        assertThat(mHalo.manager().getDevice().getAlias()).isEqualTo("myTestUser");
    }

    @Test
    public void thatSyncDeviceWhenNetworkAvailable() throws IOException {
        enqueueServerFile(mMockServer, SYNC_DEVICE);
        mHalo.getCore().manager().syncDeviceWhenNetworkAvailable(Threading.SAME_THREAD_POLICY);
        assertThat(mHalo.manager().getDevice().getAlias()).isEqualTo("myTestUser");
    }

    @Test
    public void thatSubscribeToDeviceSync() {
        mDeviceSyncSubscription = Halo.core().manager().subscribeForDeviceSync(new Subscriber() {
            @Override
            public void onEventReceived(@NonNull Event event) {
                assertThat(mDeviceSyncSubscription).isNotNull();
                mDeviceSyncSubscription.unsubscribe();
            }
        });
    }

    @Test
    public void thatCanSendDevice() throws IOException, HaloParsingException {
        enqueueServerFile(mMockServer, GET_DEVICE);
        CallbackV2<Device> callback = givenCallbackWithSendDevice(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .sendDevice()
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatIsAppAuthentication() {
        Credentials credentials = Credentials.createClient("newClient", "newSecret");
        mHalo.getCore().credentials(credentials);
        assertThat(mHalo.getCore().manager().isAppAuthentication()).isTrue();
        assertThat(mHalo.getCore().manager().isPasswordAuthentication()).isFalse();
    }

    @Test
    public void thatIsPasswordAuthentication() {
        Credentials credentials = Credentials.createUser("newClient", "newSecret");
        mHalo.getCore().credentials(credentials);
        assertThat(mHalo.getCore().manager().isAppAuthentication()).isFalse();
        assertThat(mHalo.getCore().manager().isPasswordAuthentication()).isTrue();
    }

    @Test
    public void thatStorageIsReturned() {
        assertThat(mHalo.getCore().manager().storage()).isNotNull();
    }

    @Test
    public void thatRequestAUserToken() throws IOException {
        enqueueServerFile(mMockServer, REQUEST_TOKEN);
        CallbackV2<Token> callback = givenCallbackWithRequestToken(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .requestToken(Credentials.createUser("newUser", "newPass"))
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatRequestAClientToken() throws IOException {
        enqueueServerFile(mMockServer, REQUEST_TOKEN);
        CallbackV2<Token> callback = givenCallbackWithRequestToken(mCallbackFlag);
        ICancellable cancellable = mHalo.getCore().manager()
                .requestToken(Credentials.createClient("newClient", "newSecret"))
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatCanSetNotificationsToken() throws IOException {
        enqueueServerFile(mMockServer, GET_DEVICE);
        CallbackV2<Device> callback = givenCallbackWithSetNotificationToken(mCallbackFlag);
        ICancellable cancellable = mHalo.manager().setNotificationsToken("mytoken")
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }
}
