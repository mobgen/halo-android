package com.mobgen.halo.android.twofactor.callbacks;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

/**
 * Listerner to receive updates with two fator sms codes.
 */
public class HaloTwoFactorSMS implements HaloSMSListener {
    /**
     * The halo two factor attemp listener to notify.
     */
    @NonNull
    private HaloTwoFactorAttemptListener mHaloTwoFactorAttemptListener;

    /**
     * Constructor for halo two factor sms notification.
     * @param haloTwoFactorAttemptListener
     */
    public HaloTwoFactorSMS(HaloTwoFactorAttemptListener haloTwoFactorAttemptListener){
        mHaloTwoFactorAttemptListener = haloTwoFactorAttemptListener;
    }

    @Override
    public void onSMSReceived(@NonNull Context context, @NonNull String code, @NonNull String issuer) {
        mHaloTwoFactorAttemptListener.onTwoFactorReceived(new TwoFactorCode(code,issuer));
    }
}
