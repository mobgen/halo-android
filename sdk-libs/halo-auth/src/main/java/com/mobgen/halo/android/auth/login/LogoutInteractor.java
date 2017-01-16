package com.mobgen.halo.android.auth.login;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Logout deleting account and session
 */
public class LogoutInteractor implements HaloInteractorExecutor.Interactor<Boolean> {


    private AccountManagerHelper mAccountManagerHelper;

    /**
     * Constructor for the interactor.
     *
     * @param accountManagerHelper
     */
    public LogoutInteractor(AccountManagerHelper accountManagerHelper) {
        mAccountManagerHelper = accountManagerHelper;
    }


    @NonNull
    @Override
    public HaloResultV2<Boolean> executeInteractor() throws Exception {
        HaloStatus.Builder status = HaloStatus.builder();
        if(mAccountManagerHelper!=null && mAccountManagerHelper.recoverAccount()!=null) {
            mAccountManagerHelper.removeAccount(mAccountManagerHelper.recoverAccount());
            Halo.core().flushSession();
            return new HaloResultV2<Boolean>(status.build(),new Boolean(true));
        } else {
            return new HaloResultV2<Boolean>(status.error(new Exception("Account doesnt exist")).build(),new Boolean(false));
        }
    }
}
