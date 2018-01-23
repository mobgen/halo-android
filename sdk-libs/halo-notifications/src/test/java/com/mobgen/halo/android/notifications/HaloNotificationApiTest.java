package com.mobgen.halo.android.notifications;


import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;
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
import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloNotificationsApiMock.givenAContentApi;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationActionsInstruments.givenAPushEvent;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationActionsInstruments.givenAPushEventCallback;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.givenANotificationService;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.initFirebase;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = {HaloManagerApiShadow.class})
public class HaloNotificationApiTest extends HaloRobolectricTest {

    private CallbackFlag mCallbackFlag;
    private Halo mHalo;
    private HaloNotificationsApi mNotificationsApi;
    private NotificationService mNotificationService;
    private MockServer mMockServer;

    @Override
    public void onStart() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, InstantiationException {
        initFirebase(RuntimeEnvironment.application);
        mMockServer = MockServer.create();
        mCallbackFlag = new CallbackFlag();
        mHalo = givenADefaultHalo(mMockServer.start());
        mNotificationsApi = givenAContentApi(mHalo);
        mNotificationService = givenANotificationService(RuntimeEnvironment.application);
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
        mNotificationsApi.release();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanSendAReceiptNotificationEvent() throws IOException {
        enqueueServerFile(mMockServer, RECEIPT);
        HaloPushEvent receiptPush = givenAPushEvent(NotificationEventsActions.PUSH_RECEIPT);
        CallbackV2<HaloPushEvent> callback = givenAPushEventCallback(mCallbackFlag, NotificationEventsActions.PUSH_RECEIPT);
        mNotificationsApi.notifyPushEvent(receiptPush).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSendAOpenNotificationEvent() throws IOException {
        enqueueServerFile(mMockServer, OPEN);
        HaloPushEvent openPush = givenAPushEvent(NotificationEventsActions.PUSH_OPEN);
        CallbackV2<HaloPushEvent> callback = givenAPushEventCallback(mCallbackFlag, NotificationEventsActions.PUSH_OPEN);
        mNotificationsApi.notifyPushEvent(openPush).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSendADismissNotificationEvent() throws IOException {
        enqueueServerFile(mMockServer, DISMISS);
        HaloPushEvent dismissPush = givenAPushEvent(NotificationEventsActions.PUSH_DISMISS);
        CallbackV2<HaloPushEvent> callback = givenAPushEventCallback(mCallbackFlag, NotificationEventsActions.PUSH_DISMISS);
        mNotificationsApi.notifyPushEvent(dismissPush).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }
}
