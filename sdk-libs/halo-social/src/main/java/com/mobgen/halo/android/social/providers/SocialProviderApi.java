package com.mobgen.halo.android.social.providers;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.login.LoginInteractor;
import com.mobgen.halo.android.social.login.LoginRemoteDatasource;
import com.mobgen.halo.android.social.login.LoginRepository;
import com.mobgen.halo.android.social.login.SocialLoginInteractor;
import com.mobgen.halo.android.social.models.IdentifiedUser;

/**
 * It allows to use the halo login, facebook, google plus or another declared provider.
 */
public class SocialProviderApi {

    /**
     * The account type on account manager.
     */
    private String mAccountType;
    /**
     * The recovery policy. By default recovery policy equals never store.
     */
    private int mRecoveryPolicy = 0;

    /**
     * Constructor that accepts halo.
     *
     * @param halo The halo instance.
     */
    private SocialProviderApi(@NonNull Halo halo) {
        if (halo.getCore().haloAuthRecover() != null) {
            mAccountType = halo.getCore().haloAuthRecover().accountType();
            mRecoveryPolicy = halo.getCore().haloAuthRecover().recoveryPolicy();
        }
    }

    /**
     * Tries to login with halo based on the social network token, network type.
     *
     * @param socialProviderName  The social provider name.
     * @param socialProviderToken The social provider token obtained.
     */
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<IdentifiedUser> loginWithANetwork(String socialProviderName, String socialProviderToken) {
        return new HaloInteractorExecutor<>(Halo.instance(),
                "Login with a social provider",
                new SocialLoginInteractor(mAccountType, new LoginRepository(new LoginRemoteDatasource(Halo.instance().framework().network())),
                        socialProviderName, socialProviderToken, Halo.instance().manager().getDevice().getAlias(), mRecoveryPolicy)
        );
    }

    /**
     * Tries to login with halo.
     *
     * @param username The social network to login with.
     * @param password The social token
     */
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<IdentifiedUser> loginWithHalo(@NonNull String username, @NonNull String password) {
        AssertionUtils.notNull(username, "username");
        AssertionUtils.notNull(password, "password");
        return new HaloInteractorExecutor<>(Halo.instance(),
                "Login with halo",
                new LoginInteractor(mAccountType, new LoginRepository(new LoginRemoteDatasource(Halo.instance().framework().network())),
                        username, password, Halo.instance().manager().getDevice().getAlias(), mRecoveryPolicy)
        );
    }

    /**
     * Creates the social provider api.
     *
     * @param halo The halo instance.
     * @return The social provider api instance.
     */
    @Keep
    public static Builder with(@NonNull Halo halo) {
        return new Builder(halo);
    }

    /**
     * The builder for the social provider api.
     */
    @Keep
    public static class Builder implements IBuilder<SocialProviderApi> {

        /**
         * The social provider api.
         */
        @NonNull
        private SocialProviderApi mSocialProviderApi;

        /**
         * The social provider api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull final Halo halo) {
            mSocialProviderApi = new SocialProviderApi(halo);
        }


        @NonNull
        @Override
        public SocialProviderApi build() {
            return mSocialProviderApi;
        }
    }
}