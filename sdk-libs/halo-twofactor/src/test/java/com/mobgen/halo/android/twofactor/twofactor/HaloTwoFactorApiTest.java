package com.mobgen.halo.android.twofactor.twofactor;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.services.NotificationService;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;
import com.mobgen.halo.android.twofactor.callbacks.HaloTwoFactorAttemptListener;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenAHaloTwoFactorApi;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenAHaloTwoFactorApiForNotifications;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenAHaloTwoFactorApiForSMS;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenATwoFactorAttemptListener;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenATwoFactorAttemptListenerForNotifications;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenATwoFactorAttemptListenerForSMS;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.HaloTwoFactorInstrument.givenATwoFactorAttemptListenerForSMSWithError;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.NotificationInstruments.givenANotification;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.NotificationInstruments.givenAReceivedSMSErroneousIntent;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.NotificationInstruments.givenAReceivedSMSIntent;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.NotificationInstruments.withTwoFactorData;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.NotificationServiceInstruments.givenANotificationService;
import static com.mobgen.halo.android.twofactor.mock.instrumentation.NotificationServiceInstruments.initFirebase;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by mobgenimac on 21/2/17.
 */

public class HaloTwoFactorApiTest extends HaloRobolectricTest {

    private Halo mHalo;
    private CallbackFlag mCallbackFlag;
    private NotificationService mNotificationService;
    private HaloNotificationsApi mHaloNotificationApi;

    @Override
    public void onStart() throws IOException, HaloParsingException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        initFirebase(RuntimeEnvironment.application);
        mHalo = givenADefaultHalo("mockendpoint");
        mHaloNotificationApi = HaloNotificationsApi.with(mHalo);
        mCallbackFlag = newCallbackFlag();
        mNotificationService = givenANotificationService(RuntimeEnvironment.application);

    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatCreateAHaloTwoFactorApi() {
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApi(mHalo,mHaloNotificationApi);
        assertThat(haloTwoFactorApi).isNotNull();
    }

    @Test
    public void thatCanRegisterToListenForUpdates() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withTwoFactorData());
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApi(mHalo,mHaloNotificationApi);
        haloTwoFactorApi.listenTwoFactorAttempt(givenATwoFactorAttemptListenerForNotifications(mCallbackFlag));
        mNotificationService.onMessageReceived(notification);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanRealeaseResourceAndUnsusbcribeFromReceivers() {
        Intent smsIntent = givenAReceivedSMSErroneousIntent();
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApi(mHalo,mHaloNotificationApi);
        haloTwoFactorApi.listenTwoFactorAttempt(givenATwoFactorAttemptListenerForNotifications(mCallbackFlag));
        haloTwoFactorApi.release();
        mHalo.context().sendBroadcast(smsIntent);
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }

    @Test
    public void thatListenToPushNotificationsUpdates() throws NoSuchFieldException, IllegalAccessException {
        RemoteMessage notification = givenANotification(withTwoFactorData());
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApiForNotifications(mHalo,mHaloNotificationApi);
        haloTwoFactorApi.listenTwoFactorAttempt(givenATwoFactorAttemptListenerForNotifications(mCallbackFlag));
        mNotificationService.onMessageReceived(notification);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatListenToSMSNotificationsUpdates() {
        Intent smsIntent = givenAReceivedSMSIntent(mHalo.context(),"6505551212","Hi your code is: 123456");
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApiForSMS(mHalo);
        HaloTwoFactorAttemptListener haloTwoFactorAttemptListener = givenATwoFactorAttemptListenerForSMS(mCallbackFlag);
        haloTwoFactorApi.listenTwoFactorAttempt(haloTwoFactorAttemptListener);
        mHalo.context().sendBroadcast(smsIntent);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatNoListenToSMSFromOtherSenders() {
        Intent smsIntent = givenAReceivedSMSIntent(mHalo.context(),"699322147","Hi your code is: 123456");
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApiForSMS(mHalo);
        HaloTwoFactorAttemptListener haloTwoFactorAttemptListener = givenATwoFactorAttemptListenerForSMS(mCallbackFlag);
        haloTwoFactorApi.listenTwoFactorAttempt(haloTwoFactorAttemptListener);
        mHalo.context().sendBroadcast(smsIntent);
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }

    @Test
    public void thatGetErrorCodeIfSMSIsNotWellFormed() {
        Intent smsIntent = givenAReceivedSMSErroneousIntent();
        HaloTwoFactorApi haloTwoFactorApi = givenAHaloTwoFactorApiForSMS(mHalo);
        HaloTwoFactorAttemptListener haloTwoFactorAttemptListener = givenATwoFactorAttemptListenerForSMSWithError(mCallbackFlag);
        haloTwoFactorApi.listenTwoFactorAttempt(haloTwoFactorAttemptListener);
        mHalo.context().sendBroadcast(smsIntent);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

}
