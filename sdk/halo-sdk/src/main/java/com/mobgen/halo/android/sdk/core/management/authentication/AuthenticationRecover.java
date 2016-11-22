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

    /**
     * Get the recovery policy.
     *
     * @return The recovery policy
     */
    @Keep
    @Api(2.1)
    int recoveryPolicy();

    /**
     * Get the account type.
     *
     * @return The recovery policyß
     */
    @Keep
    @Api(2.1)
    String accountType();
}
