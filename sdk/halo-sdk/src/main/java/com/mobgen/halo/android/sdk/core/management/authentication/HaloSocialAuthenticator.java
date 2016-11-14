package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

@Keep
public interface HaloSocialAuthenticator<T,S,U,E extends Exception> {

    /**
     * Tries to login with a social network based on the id of this social network.
     *
     * @param socialNetwork The social network to login with.
     * @param callback      The callback.
     *
     * @throws E SocialNotAvailableException.
     */
    @Keep
    @Api(2.0)
    void login(int socialNetwork, @NonNull CallbackV2<T> callback) throws E;

    /**
     * Tries to login with a auth profile
     *
     * @param socialNetwork The social network to login with.
     * @param haloAuthProfile   The auth profile to login on Halo.
     * @param callback      The callback.
     *
     * @throws E SocialNotAvailableException.
     */
    @Keep
    @Api(2.0)
    void login(int socialNetwork, @NonNull S haloAuthProfile, @NonNull CallbackV2<T> callback) throws E;

    /**
     * Tries to sign in with halo
     *
     * @param haloAuthProfile The auth profile to register.
     * @param haloUserProfile The user profile.
     *
     */
    @Keep
    @Api(2.0)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    HaloInteractorExecutor<T> register(@NonNull S haloAuthProfile, @NonNull U haloUserProfile);

    /**
     * Recover an account from account manager with a social network provider. Default behaviour is using Halo account.
     *
     */
    @Keep
    @Api(2.0)
    void recoverLogin();
}
