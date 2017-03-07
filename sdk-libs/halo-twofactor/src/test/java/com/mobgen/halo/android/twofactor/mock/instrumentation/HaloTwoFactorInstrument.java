package com.mobgen.halo.android.twofactor.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;
import com.mobgen.halo.android.twofactor.callbacks.HaloTwoFactorAttemptListener;
import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by mobgenimac on 21/2/17.
 */

public class HaloTwoFactorInstrument {

    public static HaloTwoFactorApi givenAHaloTwoFactorApi(Halo halo, HaloNotificationsApi haloNotificationsApi){
        return HaloTwoFactorApi.with(halo)
                .withNotifications(haloNotificationsApi)
                .withSMS()
                .build();
    }

    public static HaloTwoFactorApi givenAHaloTwoFactorApiForNotifications(Halo halo, HaloNotificationsApi haloNotificationsApi){
        return HaloTwoFactorApi.with(halo)
                .withNotifications(haloNotificationsApi)
                .build();
    }

    public static HaloTwoFactorApi givenAHaloTwoFactorApiForSMS(Halo halo){
        return HaloTwoFactorApi.with(halo)
                .smsProvider("6505551212")
                .withSMS()
                .build();
    }

    public static HaloTwoFactorAttemptListener givenATwoFactorAttemptListener(final CallbackFlag flag){
        return new HaloTwoFactorAttemptListener() {
            @Override
            public void onTwoFactorReceived(@NonNull TwoFactorCode twoFactorCode) {
                flag.flagExecuted();
            }
        };
    }

    public static HaloTwoFactorAttemptListener givenATwoFactorAttemptListenerForNotifications(final CallbackFlag flag){
        return new HaloTwoFactorAttemptListener() {
            @Override
            public void onTwoFactorReceived(@NonNull TwoFactorCode twoFactorCode) {
                assertThat(twoFactorCode.getIssuer()).isEqualTo(HaloTwoFactorApi.TWO_FACTOR_NOTIFICATION_ISSUER);
                assertThat(twoFactorCode.getCode()).isEqualTo("123456");
                flag.flagExecuted();
            }
        };
    }

    public static HaloTwoFactorAttemptListener givenATwoFactorAttemptListenerForSMS(final CallbackFlag flag){
        return new HaloTwoFactorAttemptListener() {
            @Override
            public void onTwoFactorReceived(@NonNull TwoFactorCode twoFactorCode) {
                assertThat(twoFactorCode.getIssuer()).isEqualTo(HaloTwoFactorApi.TWO_FACTOR_SMS_ISSUER);
                assertThat(twoFactorCode.getCode()).isEqualTo("123456");
                flag.flagExecuted();
            }
        };
    }

    public static HaloTwoFactorAttemptListener givenATwoFactorAttemptListenerForSMSWithError(final CallbackFlag flag){
        return new HaloTwoFactorAttemptListener() {
            @Override
            public void onTwoFactorReceived(@NonNull TwoFactorCode twoFactorCode) {
                assertThat(twoFactorCode.getIssuer()).isEqualTo(HaloTwoFactorApi.TWO_FACTOR_SMS_ISSUER);
                assertThat(twoFactorCode.getCode()).isEqualTo("-1");
                flag.flagExecuted();
            }
        };
    }
}
