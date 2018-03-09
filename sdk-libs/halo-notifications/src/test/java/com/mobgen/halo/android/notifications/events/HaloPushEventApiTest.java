package com.mobgen.halo.android.notifications.events;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.notifications.mock.instrumentation.HaloManagerApiShadow;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.notifications.services.NotificationService;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.mobgen.halo.android.notifications.fixtures.ServerFixtures.DISMISS;
import static com.mobgen.halo.android.notifications.fixtures.ServerFixtures.OPEN;
import static com.mobgen.halo.android.notifications.fixtures.ServerFixtures.RECEIPT;
import static com.mobgen.halo.android.notifications.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloNotificationsApiMock.givenANotificationEventApi;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationActionsInstruments.givenAPushEvent;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationActionsInstruments.givenAPushEventCallback;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.givenANotificationService;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.initFirebase;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 12/02/2018.
 */
@Config(shadows = {HaloManagerApiShadow.class})
public class HaloPushEventApiTest extends HaloRobolectricTest {

    private CallbackFlag mCallbackFlag;
    private Halo mHalo;
    private HaloPushEventsApi pushEventApi;
    private NotificationService mNotificationService;
    private MockServer mMockServer;

    @Override
    public void onStart() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, InstantiationException {
        initFirebase(RuntimeEnvironment.application);
        mMockServer = MockServer.create();
        mCallbackFlag = new CallbackFlag();
        mHalo = givenADefaultHalo(mMockServer.start());
        pushEventApi = givenANotificationEventApi(mHalo);
        mNotificationService = givenANotificationService(RuntimeEnvironment.application);
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanSendAReceiptNotificationEvent() throws IOException {
        enqueueServerFile(mMockServer, RECEIPT);
        HaloPushEvent receiptPush = givenAPushEvent(NotificationEventsActions.PUSH_RECEIPT);
        CallbackV2<HaloPushEvent> callback = givenAPushEventCallback(mCallbackFlag, NotificationEventsActions.PUSH_RECEIPT);
        pushEventApi.notifyPushEvent(receiptPush).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSendAOpenNotificationEvent() throws IOException {
        enqueueServerFile(mMockServer, OPEN);
        HaloPushEvent openPush = givenAPushEvent(NotificationEventsActions.PUSH_OPEN);
        CallbackV2<HaloPushEvent> callback = givenAPushEventCallback(mCallbackFlag, NotificationEventsActions.PUSH_OPEN);
        pushEventApi.notifyPushEvent(openPush).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSendADismissNotificationEvent() throws IOException {
        enqueueServerFile(mMockServer, DISMISS);
        HaloPushEvent dismissPush = givenAPushEvent(NotificationEventsActions.PUSH_DISMISS);
        CallbackV2<HaloPushEvent> callback = givenAPushEventCallback(mCallbackFlag, NotificationEventsActions.PUSH_DISMISS);
        pushEventApi.notifyPushEvent(dismissPush).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

}
