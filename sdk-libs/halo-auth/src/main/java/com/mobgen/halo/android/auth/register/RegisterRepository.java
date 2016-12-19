package com.mobgen.halo.android.auth.register;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.HaloUserProfile;

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
     *
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
     * @throws HaloParsingException Parsing exception.
     */
    @NonNull
    public HaloResultV2<HaloUserProfile> registerHalo(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloUserProfile haloUserProfileResponse = null;
        try {
            haloUserProfileResponse = mRemoteDatasource.register(haloAuthProfile, haloUserProfile);
        } catch (HaloNetException haloNetException) {
            status.error(haloNetException);
        } catch (HaloParsingException haloParsingException) {
            status.error(haloParsingException);
        }
        return new HaloResultV2<>(status.build(), haloUserProfileResponse);
    }
}
