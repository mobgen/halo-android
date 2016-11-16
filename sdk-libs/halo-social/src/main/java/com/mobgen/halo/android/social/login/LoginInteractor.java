package com.mobgen.halo.android.social.login;

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

/**
 * Login with halo
 */
public class LoginInteractor implements HaloInteractorExecutor.Interactor<IdentifiedUser> {

    /**
     * Login repository.
     */
    private LoginRepository mLoginRepository;
    /**
     * User email.
     */
    private String mUserName;
    /**
     * User password.
     */
    private String mPassword;
    /**
     * The device alias.
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
     * @param username The user email.
     * @param password The password.
     * @param deviceAlias The device alias.
     */
    public LoginInteractor(String accountType, LoginRepository loginRepository, String username, String password, String deviceAlias, int recoveryPolicy) {
        mAccountType=accountType;
        mLoginRepository = loginRepository;
        mUserName = username;
        mPassword = password;
        mDeviceAlias = deviceAlias;
        mRecoveryPolicy=recoveryPolicy;
    }


    @NonNull
    @Override
    public HaloResultV2<IdentifiedUser> executeInteractor() throws Exception {
        HaloStatus.Builder status = HaloStatus.builder();
        IdentifiedUser identifiedUser = null;
        try {
            identifiedUser = mLoginRepository.loginHalo(mUserName,mPassword,mDeviceAlias);
            //store user credentials on account manager
            if(mRecoveryPolicy == HaloSocialApi.RECOVERY_ALWAYS) {
                if (mAccountType != null) {
                    AccountManagerHelper accountManagerHelper = new AccountManagerHelper(Halo.instance().context());
                    accountManagerHelper.addAccountWithPassword(mAccountType, AuthTokenType.HALO_AUTH_TOKEN, mUserName, mPassword, identifiedUser.getToken().getAccessToken());
                }
            }
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), identifiedUser);

    }
}
