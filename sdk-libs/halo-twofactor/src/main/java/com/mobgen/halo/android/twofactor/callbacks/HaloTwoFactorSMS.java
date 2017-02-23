package com.mobgen.halo.android.twofactor.callbacks;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

/**
 * Created by mobgenimac on 20/2/17.
 */

public class HaloTwoFactorSMS implements HaloSMSListener {

    private HaloTwoFactorAttemptListener mHaloTwoFactorAttemptListener;

    public HaloTwoFactorSMS(HaloTwoFactorAttemptListener haloTwoFactorAttemptListener){
        mHaloTwoFactorAttemptListener = haloTwoFactorAttemptListener;
    }

    @Override
    public void onSMSReceived(@NonNull Context context, @NonNull String code, @NonNull String issuer) {
        mHaloTwoFactorAttemptListener.onTwoFactorReceived(new TwoFactorCode(code,issuer));
    }
}
