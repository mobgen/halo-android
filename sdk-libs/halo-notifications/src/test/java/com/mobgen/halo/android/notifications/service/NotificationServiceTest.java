package com.mobgen.halo.android.notifications.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.services.InstanceIDService;
import com.mobgen.halo.android.notifications.services.NotificationEmitter;
import com.mobgen.halo.android.notifications.services.NotificationService;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloNotificationsApiMock.givenAContentApi;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationDecoratorInstruments.givenADefaultNotificationDecorator;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationDecoratorInstruments.givenANullReturningDecorator;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.givenANotification;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.withAnySourceNotification;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.withExtraData;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.withExtraDataJSON;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.withNotSilentNotification;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.withNullExtraData;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationInstruments.withSilentNotification;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationListenerInstruments.givenANotificationListener;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationListenerInstruments.givenAnAllNotificationListener;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.givenANotificationService;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.givenTheInstanceIDService;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationServiceInstruments.initFirebase;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public class NotificationServiceTest extends HaloRobolectricTest {

    private CallbackFlag mCallbackFlag;
    private Halo mHalo;
    private HaloNotificationsApi mNotificationsApi;
    private NotificationService mNotificationService;

    @Override
    public void onStart() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, InstantiationException {
        initFirebase(RuntimeEnvironment.application);
        mCallbackFlag = new CallbackFlag();
        mHalo = givenADefaultHalo();
        mNotificationsApi = givenAContentApi(mHalo);
        mNotificationService = givenANotificationService(RuntimeEnvironment.application);
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
        mNotificationsApi.release();
    }

    @Test
    public void thatConstructorIsPrivateOnEmitter() throws Exception {
        TestUtils.testPrivateConstructor(NotificationEmitter.class);
    }

    @Test
    public void thatANotificationWithNullExtra() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withNullExtraData());
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, false));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }


    @Test
    public void thatANotificationWithExtraDataInJSON() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withExtraDataJSON());
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, false));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }

    @Test
    public void thatANotificationWithExtraDataString() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withExtraData());
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, false));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }

    @Test
    public void thatASilentNotificationIsReceivedAndNotDisplayed() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withSilentNotification());
        ISubscription subscription = mNotificationsApi.listenSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, true));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }

    @Test
    public void thatANotSilentNotificationIsReceivedAndDisplayed() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withNotSilentNotification());
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, false));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }

    @Test
    public void thatBothNotificationsAreReceived() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage silentNotification = givenANotification(withSilentNotification());
        RemoteMessage notSilentNotification = givenANotification(withNotSilentNotification());
        ISubscription subscription = mNotificationsApi.listenAllNotifications(givenAnAllNotificationListener(mCallbackFlag));

        mNotificationService.onMessageReceived(notSilentNotification);
        mNotificationService.onMessageReceived(silentNotification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        subscription.unsubscribe();
    }

    @Test
    public void thatNotificationIdIsPresentForANotSilentNotification() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withNotSilentNotification());
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, false));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }

    @Test
    public void thatANullReturnInDecoratorAvoidsNotificationToPresent() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withNotSilentNotification());
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, true));
        mNotificationsApi.setNotificationDecorator(givenANullReturningDecorator());

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        subscription.unsubscribe();
    }

    @Test
    public void thatADecoratorIsNotExecutedWithSilentNotifications() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withSilentNotification());
        ISubscription subscription = mNotificationsApi.listenSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, true));
        mNotificationsApi.setNotificationDecorator(givenADefaultNotificationDecorator(mCallbackFlag));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        subscription.unsubscribe();
    }

    @Test
    public void thatADecoratorIsExecutedWithNotSilentNotifications() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withNotSilentNotification());
        mNotificationsApi.setNotificationDecorator(givenADefaultNotificationDecorator(mCallbackFlag));
        ISubscription subscription = mNotificationsApi.listenNotSilentNotifications(givenANotificationListener(mNotificationsApi, mCallbackFlag, false));

        mNotificationService.onMessageReceived(notification);

        assertThat(subscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        subscription.unsubscribe();
    }

    @Test
    public void thatRefreshTokenWorksProperly() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InstanceIDService instanceIDService = givenTheInstanceIDService(RuntimeEnvironment.application);
        String oldToken = mNotificationsApi.token();
        when(FirebaseInstanceId.getInstance().getToken()).thenReturn("newToken");
        instanceIDService.onTokenRefresh();
        String newToken = mNotificationsApi.token();
        assertThat(oldToken).isNotEqualTo(newToken);
    }

    @Test
    public void thatNormalCreationHasAValidInstance() {
        HaloNotificationsApi api = HaloNotificationsApi.with(mHalo);
        assertThat(api).isNotNull();
        api.release();
    }

    @Test
    public void thatFromSourceIsNeverNullable() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withAnySourceNotification());
        ISubscription subscription = mNotificationsApi.listenAllNotifications(givenAnAllNotificationListener(mCallbackFlag));

        mNotificationService.onMessageReceived(notification);

        assertThat(mCallbackFlag.isFlagged());
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(1);
        subscription.unsubscribe();
    }
}
