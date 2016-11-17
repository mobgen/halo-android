package com.mobgen.halo.android.social.register;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;

/**
 * Sign in with halo
 */
public class RegisterInteractor implements HaloInteractorExecutor.Interactor<HaloUserProfile> {

    /**
     * Register repository.
     */
    private RegisterRepository mRegisterRepository;
    /**
     * Auth profile.
     */
    private HaloAuthProfile mHaloAuthProfile;
    /**
     * The user profile.
     */
    private HaloUserProfile mHaloUserProfile;

    /**
     * Constructor for the interactor.
     *
     * @param registerRepository The register repository.
     * @param haloAuthProfile    The auth profile.
     * @param haloUserProfile    The user profile.
     */
    public RegisterInteractor(@NonNull RegisterRepository registerRepository, @NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) {
        mRegisterRepository = registerRepository;
        mHaloAuthProfile = haloAuthProfile;
        mHaloUserProfile = haloUserProfile;
    }


    @NonNull
    @Override
    public HaloResultV2<HaloUserProfile> executeInteractor() throws Exception {
        return mRegisterRepository.registerHalo(mHaloAuthProfile, mHaloUserProfile);
    }
}
