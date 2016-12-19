package com.mobgen.halo.android.auth.login;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.auth.models.IdentifiedUser;

/**
 * The login repository to provide.
 */
public class LoginRepository {

    /**
     * Remote data source.
     */
    private LoginRemoteDatasource mRemoteDatasource;

    /**
     * Constructor for the repository.
     *
     * @param loginRemoteDatasource The remote data source.
     */
    public LoginRepository(@NonNull LoginRemoteDatasource loginRemoteDatasource) {
        AssertionUtils.notNull(loginRemoteDatasource, "remoteDatasource");
        mRemoteDatasource = loginRemoteDatasource;
    }

    /**
     * Provides the user identity with a social token.
     *
     * @param socialApiName The social api name
     * @param socialToken   The social token
     * @param deviceAlias   The device alias
     * @return The user identified
     */
    @NonNull
    public HaloResultV2<IdentifiedUser> loginSocialProvider(@NonNull String socialApiName, @NonNull String socialToken, @NonNull String deviceAlias) throws HaloNetException {
        HaloStatus.Builder status = HaloStatus.builder();
        IdentifiedUser identifiedUser = null;
        try {
            identifiedUser = mRemoteDatasource.loginSocial(socialApiName, socialToken, deviceAlias);
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), identifiedUser);

    }

    /**
     * Provides the user identity.
     *
     * @param username    The username.
     * @param password    The password.
     * @param deviceAlias The device alias.
     * @return The user identified
     */
    @NonNull
    public HaloResultV2<IdentifiedUser> loginHalo(@NonNull String username, @NonNull String password, @NonNull String deviceAlias) throws HaloNetException {
        HaloStatus.Builder status = HaloStatus.builder();
        IdentifiedUser identifiedUser = null;
        try {
            identifiedUser = mRemoteDatasource.loginHalo(username, password, deviceAlias);
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), identifiedUser);
    }
}
