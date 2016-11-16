package com.mobgen.halo.android.social.login;

import android.accounts.Account;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.social.authenticator.AuthTokenType;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.social.providers.google.GoogleSocialProvider;


/**
 * Login with different network providers.
 */
public class SocialLoginInteractor implements HaloInteractorExecutor.Interactor<IdentifiedUser> {

    /**
     * The login repository.
     */
    private LoginRepository mLoginRepository;
    /**
     * The social network provider name.
     */
    private String mSocialProviderName;
    /**
     * The social network token.
     */
    private String mSocialToken;
    /**
     * The device alais
     */
    private String mDeviceAlias;
    /**
     * The account type in account manager
     */
    private String mAccountType;
    /**
     * The recovery from account manager policy
     */
    private int mRecoveryPolicy;

    /**
     * Constructor for the interactor.
     *
     * @param accountType The account type in account manager.
     * @param loginRepository The login repository.
     * @param socialApiName The social api name.
     * @param socialToken The social network token.
     * @param deviceAlias The device alias.
     */
    public SocialLoginInteractor(String accountType, LoginRepository loginRepository, String socialApiName, String socialToken, String deviceAlias, int recoveryPolicy) {
        mAccountType = accountType;
        mLoginRepository = loginRepository;
        mSocialProviderName = socialApiName;
        mSocialToken = socialToken;
        mDeviceAlias = deviceAlias;
        mRecoveryPolicy=recoveryPolicy;
    }


    @NonNull
    @Override
    public HaloResultV2<IdentifiedUser> executeInteractor() throws Exception {
        HaloStatus.Builder status = HaloStatus.builder();
        IdentifiedUser identifiedUser = null;
        try {
            identifiedUser = mLoginRepository.loginSocialProvider(mSocialProviderName, mSocialToken, mDeviceAlias);
            //store user credentials on account manager
            if(mRecoveryPolicy == HaloSocialApi.RECOVERY_ALWAYS) {
                if (mAccountType != null) {
                    AccountManagerHelper accountManagerHelper = new AccountManagerHelper(Halo.instance().context());
                    if (mSocialProviderName.equals(FacebookSocialProvider.SOCIAL_FACEBOOK_NAME)) {
                        accountManagerHelper.addAccountToken(mAccountType, AuthTokenType.FACEBOOK_AUTH_TOKEN, identifiedUser.getUser().getEmail(), mSocialToken,identifiedUser.getToken().getAccessToken());
                    } else if (mSocialProviderName.equals(GoogleSocialProvider.SOCIAL_GOOGLE_PLUS_NAME)) {
                        accountManagerHelper.addAccountToken(mAccountType, AuthTokenType.GOOGLE_AUTH_TOKEN, identifiedUser.getUser().getEmail(), mSocialToken,identifiedUser.getToken().getAccessToken());
                    }
                }
            }
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), identifiedUser);

    }
}
