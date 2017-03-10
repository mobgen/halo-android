package com.mobgen.halo.android.twofactor.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;

/**
 * Provides a callback to make some operation over every notification item.
 */
@Keep
public interface HaloSMSListener {

        /**
         * Callback to perform some operation when a notification arrives tho the system.
         *
         * @param context The context.
         */
        @Keep
        @Api(2.3)
        void onSMSReceived(@NonNull Context context, @NonNull String code, @NonNull @HaloTwoFactorApi.IssuerType String issuer);
}
