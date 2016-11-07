package com.mobgen.halo.android.social.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.google.HaloGoogleSignInActivity;
import com.mobgen.halo.android.social.providers.halo.HaloSocialProvider;

/**
 * Sign in with halo
 */
public class RegisterInteractor implements HaloInteractorExecutor.Interactor<HaloSocialProfile> {

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
     * @param registerRepository The register repository.
     * @param haloAuthProfile The auth profile.
     * @param haloUserProfile The user profile.
     */
    public RegisterInteractor(@NonNull RegisterRepository registerRepository, @NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) {
        mRegisterRepository = registerRepository;
        mHaloAuthProfile = haloAuthProfile;
        mHaloUserProfile = haloUserProfile;
    }


    @NonNull
    @Override
    public HaloResultV2<HaloSocialProfile> executeInteractor() throws Exception {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloUserProfile haloUserProfile = null;
        try {
            haloUserProfile = mRegisterRepository.registerHalo(mHaloAuthProfile, mHaloUserProfile);
        } catch (HaloNetException e) {
            status.error(e);
        }
        return processResult(new HaloResultV2<>(status.build(), haloUserProfile));

    }

    /**
     * Convert HaloUserProfile to HaloSocialProfile
     *
     * @param identifiedUserResult The identified user result.
     *
     * @return The result created.
     */
    private HaloResultV2<HaloSocialProfile> processResult(@Nullable HaloResultV2<HaloUserProfile> identifiedUserResult){
        HaloSocialProfile profile=null;
        if(identifiedUserResult.status().isOk()) {
            HaloUserProfile haloUserProfile = identifiedUserResult.data();
            profile = HaloSocialProfile.builder()
                    .socialName(HaloSocialProvider.SOCIAL_HALO_NAME)
                    .socialId(haloUserProfile.getIdentifiedId())
                    .name(haloUserProfile.getName())
                    .surname(haloUserProfile.getSurname())
                    .displayName(haloUserProfile.getDisplayName())
                    .email(haloUserProfile.getEmail())
                    .photo(haloUserProfile.getPhoto())
                    .build();
        }
        return new HaloResultV2<>(identifiedUserResult.status(), profile);

    }
}
