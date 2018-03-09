package com.mobgen.halo.android.notifications.events;

import android.content.Intent;
import android.content.IntentFilter;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationEventListener;
import com.mobgen.halo.android.notifications.mock.instrumentation.HaloManagerApiShadow;
import com.mobgen.halo.android.notifications.services.NotificationEmitter;
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
import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloNotificationsApiMock.givenANotificationApi;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationActionIntentInstruments.givenAEventActionIntent;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationListenerInstruments.givenANotificationEventActionListenerWithAction;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.givenANotificationService;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.initFirebase;
import static com.mobgen.halo.android.notifications.services.NotificationEmitter.NOTIFICATION_EVENT;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 23/01/2018.
 */
@Config(shadows = {HaloManagerApiShadow.class})
public class NotificationEventSubscriptionTest extends HaloRobolectricTest {

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
        mNotificationsApi = givenANotificationApi(mHalo);
        mNotificationService = givenANotificationService(RuntimeEnvironment.application);
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
        mNotificationsApi.release();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanHandleADismissAction() throws IOException {
        enqueueServerFile(mMockServer, DISMISS);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HaloUtils.getEventName(Halo.instance().context(), NOTIFICATION_EVENT) + NotificationEventsActions.PUSH_DISMISS);
        HaloNotificationEventListener haloNotificationEventListener = givenANotificationEventActionListenerWithAction(mCallbackFlag, NotificationEventsActions.PUSH_DISMISS);
        NotificationEventSubscription notificationEventSubscription = new NotificationEventSubscription(RuntimeEnvironment.application, haloNotificationEventListener, intentFilter);

        Intent deleteIntent = givenAEventActionIntent(NotificationEventsActions.PUSH_DISMISS, "scheduleID");

        NotificationEmitter.emitNotificationEventAction(RuntimeEnvironment.application, deleteIntent);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAReceiptAction() throws IOException {
        enqueueServerFile(mMockServer, RECEIPT);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HaloUtils.getEventName(Halo.instance().context(), NOTIFICATION_EVENT) + NotificationEventsActions.PUSH_RECEIPT);

        HaloNotificationEventListener haloNotificationEventListener = givenANotificationEventActionListenerWithAction(mCallbackFlag, NotificationEventsActions.PUSH_RECEIPT);
        NotificationEventSubscription notificationEventSubscription = new NotificationEventSubscription(RuntimeEnvironment.application, haloNotificationEventListener, intentFilter);

        Intent deleteIntent = givenAEventActionIntent(NotificationEventsActions.PUSH_RECEIPT, "scheduleID");

        NotificationEmitter.emitNotificationEventAction(RuntimeEnvironment.application, deleteIntent);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAOpenAction() throws IOException {
        enqueueServerFile(mMockServer, OPEN);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HaloUtils.getEventName(Halo.instance().context(), NOTIFICATION_EVENT) + NotificationEventsActions.PUSH_OPEN);
        HaloNotificationEventListener haloNotificationEventListener = givenANotificationEventActionListenerWithAction(mCallbackFlag, NotificationEventsActions.PUSH_OPEN);
        NotificationEventSubscription notificationEventSubscription = new NotificationEventSubscription(RuntimeEnvironment.application, haloNotificationEventListener, intentFilter);

        Intent deleteIntent = givenAEventActionIntent(NotificationEventsActions.PUSH_OPEN, "scheduleID");

        NotificationEmitter.emitNotificationEventAction(RuntimeEnvironment.application, deleteIntent);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }
}
