package com.mobgen.halo.android.twofactor.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;
import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

/**
 * Listerner to receive updates with two fator push notification codes.
 */
public class HaloTwoFactorNotification implements HaloNotificationListener {

    /**
     * The key to extract the code from received bundle.
     */
    @NonNull
    private static final String KEY_FROM_BUNDLE = "code";

    /**
     * The halo two factor attemp listener to notify.
     */
    @NonNull
    private HaloTwoFactorAttemptListener mHaloTwoFactorAttemptListener;

    /**
     * Construcotr for halo two factor push notification.
     *
     * @param haloTwoFactorAttemptListener
     */
    public HaloTwoFactorNotification(@NonNull HaloTwoFactorAttemptListener haloTwoFactorAttemptListener){
        mHaloTwoFactorAttemptListener = haloTwoFactorAttemptListener;
    }

    @Override
    public void onNotificationReceived(@NonNull Context context, @NonNull String s, @NonNull Bundle bundle, @Nullable Bundle bundle1) {
        mHaloTwoFactorAttemptListener.onTwoFactorReceived(new TwoFactorCode(bundle.getString(KEY_FROM_BUNDLE), HaloTwoFactorApi.TWO_FACTOR_NOTIFICATION_ISSUER));
    }
}
