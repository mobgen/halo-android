package com.mobgen.halo.android.twofactor.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;
import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

/**
 * Created by fernando souto on 20/2/17.
 */

public class HaloTwoFactorNotification implements HaloNotificationListener {

    private HaloTwoFactorAttemptListener mHaloTwoFactorAttemptListener;

    public HaloTwoFactorNotification(HaloTwoFactorAttemptListener haloTwoFactorAttemptListener){
        mHaloTwoFactorAttemptListener = haloTwoFactorAttemptListener;
    }

    @Override
    public void onNotificationReceived(@NonNull Context context, @NonNull String s, @NonNull Bundle bundle, @Nullable Bundle bundle1) {
        mHaloTwoFactorAttemptListener.onTwoFactorReceived(new TwoFactorCode(bundle.getString("code"), HaloTwoFactorApi.TWO_FACTOR_NOTIFICATION_ISSUER));
    }
}
