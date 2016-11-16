package com.mobgen.halo.android.social.login;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.social.models.IdentifiedUser;

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
     * @param socialToken The social token
     * @param deviceAlias The device alias
     * @return The user identified
     */
    @NonNull
    public synchronized IdentifiedUser loginSocialProvider(@NonNull String socialApiName, @NonNull String socialToken, @NonNull String deviceAlias) throws HaloNetException {
        return mRemoteDatasource.loginSocial(socialApiName,socialToken,deviceAlias);
    }

    /**
     * Provides the user identity.
     *
     * @param username The username.
     * @param password The password.
     * @param deviceAlias The device alias.
     * @return The user identified
     */
    @NonNull
    public synchronized IdentifiedUser loginHalo(@NonNull String username, @NonNull String password,@NonNull String deviceAlias) throws HaloNetException {
        return mRemoteDatasource.login(username,password,deviceAlias);
    }
}
