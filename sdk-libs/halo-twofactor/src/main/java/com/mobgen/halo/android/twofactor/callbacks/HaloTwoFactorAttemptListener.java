package com.mobgen.halo.android.twofactor.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

/**
 * Provides a callback to make some operation over every notification item from two factor.
 */
@Keep
public interface HaloTwoFactorAttemptListener {

    /**
     * Callback to perform some operation when a notification arrives tho the system.
     *
     * @param twoFactorCode The two factor code.
     */
    @Keep
    @Api(2.3)
    void onTwoFactorReceived(@NonNull TwoFactorCode twoFactorCode);
}
