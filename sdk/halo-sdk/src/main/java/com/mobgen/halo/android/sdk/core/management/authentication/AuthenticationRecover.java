package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;
/**
 * Authentication recover to retrieve account from account manager when exist.
 */
@Keep
public interface AuthenticationRecover {

    /**
     * Recover an account from account manager with a social network provider. Default behaviour is using Halo account.
     */
    @Keep
    @Api(2.1)
    void recoverAccount();
}
