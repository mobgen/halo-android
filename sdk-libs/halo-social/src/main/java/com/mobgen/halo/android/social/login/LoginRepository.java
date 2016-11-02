package com.mobgen.halo.android.social.login;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.management.models.Device;
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
     * @param socialApiName The remote data source.
     * @param socialToken The remote data source.
     * @param device The remote data source.
     * @return The user identified
     */
    @NonNull
    public synchronized IdentifiedUser loginSocialProvider(@NonNull String socialApiName, @NonNull String socialToken, @NonNull Device device) throws HaloNetException {
        return mRemoteDatasource.loginSocial(socialApiName,socialToken,device.getAlias());
    }

    /**
     * Provides the user identity.
     *
     * @param username The remote data source.
     * @param password The remote data source.
     * @param device The remote data source.
     * @return The user identified
     */
    @NonNull
    public synchronized IdentifiedUser loginHalo(@NonNull String username, @NonNull String password,@NonNull Device device) throws HaloNetException {
        return mRemoteDatasource.login(username,password,device.getAlias());
    }
}
