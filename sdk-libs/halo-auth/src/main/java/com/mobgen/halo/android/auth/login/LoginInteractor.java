package com.mobgen.halo.android.auth.login;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.Session;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.auth.models.IdentifiedUser;

import static com.mobgen.halo.android.sdk.core.management.authentication.HaloAuthenticator.HALO_SESSION_NAME;

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
     * The account type.
     */
    private String mAccountType;
    /**
     * The recovery from account manager policy
     */
    private int mRecoveryPolicy;

    /**
     * Constructor for the interactor.
     *
     * @param accountType The account type.
     * @param loginRepository      The login repository.
     * @param username             The user email.
     * @param password             The password.
     * @param deviceAlias          The device alias.
     */
    public LoginInteractor(String accountType, LoginRepository loginRepository, String username, String password, String deviceAlias, int recoveryPolicy) {
        mAccountType = accountType;
        mLoginRepository = loginRepository;
        mUserName = username;
        mPassword = password;
        mDeviceAlias = deviceAlias;
        mRecoveryPolicy = recoveryPolicy;
    }


    @NonNull
    @Override
    public HaloResultV2<IdentifiedUser> executeInteractor() throws Exception {
        HaloResultV2<IdentifiedUser> response = mLoginRepository.loginHalo(mUserName, mPassword, mDeviceAlias);
        //store user credentials on account manager
        if (mRecoveryPolicy == HaloAuthApi.RECOVERY_ALWAYS && response.status().isOk()) {
            if (mAccountType != null) {
                AccountManagerHelper accountManagerHelper = new AccountManagerHelper(Halo.instance().context(),mAccountType);
                accountManagerHelper.addAccountWithPassword(AccountManagerHelper.HALO_AUTH_PROVIDER, mUserName, mPassword, response.data().getToken());
                //save session
                Session session = new Session(response.data().getToken());
                Halo.core().sessionManager().setSession(HALO_SESSION_NAME, session);
                Halo.instance().getCore().haloAuthRecover().recoverStatus(false);
            }
        }
        return response;

    }
}
