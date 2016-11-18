package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Halo social api to login and register with social providers.
 */
@Keep
public interface HaloSocial<T, S, U, E extends Exception> {

    /**
     * Tries to login with a social network based on the id of this social network, and auth profile
     *
     * @param socialNetwork   The social network to login with.
     * @param haloAuthProfile The auth profile to login on Halo.
     * @param callback        The callback.
     * @throws E SocialNotAvailableException.
     */
    @Keep
    @Api(2.1)
    void loginWithHalo(int socialNetwork, @NonNull S haloAuthProfile, @NonNull CallbackV2<T> callback) throws E;

    /**
     * Tries to login with a social network based on the id of this social network.
     *
     * @param socialNetwork The social network to login with.
     * @param callback      The callback.
     * @throws E SocialNotAvailableException.
     */
    @Keep
    @Api(2.1)
    void loginWithSocial(int socialNetwork, @NonNull CallbackV2<T> callback) throws E;

    /**
     * Tries to sign in with halo
     *
     * @param haloAuthProfile The auth profile to register.
     * @param haloUserProfile The user profile.
     */
    @Keep
    @Api(2.1)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    HaloInteractorExecutor<U> register(@NonNull S haloAuthProfile, @NonNull U haloUserProfile);

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
