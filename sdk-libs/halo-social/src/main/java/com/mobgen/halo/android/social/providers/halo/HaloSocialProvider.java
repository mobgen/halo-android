package com.mobgen.halo.android.social.providers.halo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.providers.SocialProvider;
import com.mobgen.halo.android.social.models.IdentifiedUser;

/**
 * The social provider for halo.
 */
public class HaloSocialProvider implements SocialProvider {
    /**
     * Social api
     */
    private HaloSocialApi mSocialApi;
    /**
     * Name for social login with halo.
     */
    public static final String SOCIAL_HALO_NAME = "halo";
    /**
     * Event subscription to listen for the login provider.
     */
    private ISubscription mAuthenticationSubscription;
    /**
     * The callback to provide the result.
     */
    private CallbackV2<HaloSocialProfile> mCallback;
    /**
     * The auth profile
     */
    private HaloAuthProfile mHaloAuthProfile;

    /**
     * Constructor for the social provider for halo
     *
     */
    public HaloSocialProvider() {

    }

    @Override
    public boolean isLibraryAvailable(@NonNull Context context) {
        return true;
    }

    @Override
    public String getSocialNetworkName() {
        return SOCIAL_HALO_NAME;
    }

    @Override
    public boolean linkedAppAvailable(@NonNull Context context) {
        return true; //No app is needed
    }

    @Override
    public void setAuthProfile(@NonNull HaloAuthProfile haloAuthProfile){
        mHaloAuthProfile = haloAuthProfile;
    }

    @Override
    public void authenticate(@NonNull Halo halo, @NonNull String accountType, @NonNull CallbackV2<HaloSocialProfile> callback) {

            mCallback = callback;
            mSocialApi = HaloSocialApi.with(Halo.instance())
                    .storeCredentials(accountType)
                    .withHalo()
                    .build();
            mSocialApi.loginWithHalo(mHaloAuthProfile.getEmail(), mHaloAuthProfile.getPassword())
                    .execute(new CallbackV2<IdentifiedUser>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                            mCallback.onFinish(processResult(result));
                        }
                    });
    }

    private HaloResultV2<HaloSocialProfile> processResult(HaloResultV2<IdentifiedUser> result){
        HaloSocialProfile profile=null;
        if(result.status().isOk()) {
            IdentifiedUser identifiedUser = result.data();
            profile = HaloSocialProfile.builder(identifiedUser.getToken().getRefreshToken())
                    .socialName(getSocialNetworkName())
                    .socialId(identifiedUser.getUser().getIdentifiedId())
                    .name(identifiedUser.getUser().getName())
                    .surname(identifiedUser.getUser().getSurname())
                    .displayName(identifiedUser.getUser().getDisplayName())
                    .email(identifiedUser.getUser().getEmail())
                    .photo(identifiedUser.getUser().getPhoto())
                    .build();
        }
        return new HaloResultV2<>(result.status(), profile);

    }

    @Override
    public void release() {
    }

}
