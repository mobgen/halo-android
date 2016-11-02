package com.mobgen.halo.android.social.register;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;

/**
 * The sign in repository to provide.
 */
public class RegisterRepository {

    /**
     * Remote data source.
     */
    private RegisterRemoteDatasource mRemoteDatasource;

    /**
     * Constructor for the repository.
     * @param registerRemoteDatasource The remote data source.
     */
    public RegisterRepository(@NonNull RegisterRemoteDatasource registerRemoteDatasource) {
        AssertionUtils.notNull(registerRemoteDatasource, "remoteDatasource");
        mRemoteDatasource = registerRemoteDatasource;
    }

    /**
     * Sign in a user in halo
     *
     * @param haloAuthProfile The auth profile
     * @param haloUserProfile The user profile
     * @return The user identified
     * @throws HaloNetException Networking exception.
     */
    @NonNull
    public synchronized IdentifiedUser registerHalo(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) throws HaloNetException {
        return mRemoteDatasource.register(haloAuthProfile, haloUserProfile);
    }
}
