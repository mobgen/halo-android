package com.mobgen.halo.android.social.providers.halo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialProvider;
import com.mobgen.halo.android.social.providers.SocialProviderApi;

/**
 * The social provider for halo.
 */
public class HaloSocialProvider implements SocialProvider {
    /**
     * Name for social login with halo.
     */
    public static final String SOCIAL_HALO_NAME = "halo";
    /**
     * The callback to provide the result.
     */
    private CallbackV2<IdentifiedUser> userRequestCallbak;
    /**
     * The social providera api.
     */
    private SocialProviderApi mSocialProviderApi;

    /**
     * Constructor for the social provider for halo
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
    public void setSocialToken(@NonNull String socialToken) {
        return;
    }

    @Override
    public void release() {
        return;
    }

    @Override
    public void authenticate(@NonNull Halo halo, @Nullable HaloAuthProfile haloAuthProfile, @Nullable CallbackV2<IdentifiedUser> callback) {
        AssertionUtils.notNull(haloAuthProfile, "haloAuthProfile");
        userRequestCallbak = callback;
        mSocialProviderApi = SocialProviderApi.with(halo)
                .build();
        mSocialProviderApi.loginWithHalo(haloAuthProfile.getEmail(), haloAuthProfile.getPassword())
                .execute(new CallbackV2<IdentifiedUser>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                        if (userRequestCallbak != null) {
                            userRequestCallbak.onFinish(result);
                        }
                    }
                });
    }

    private HaloResultV2<IdentifiedUser> error(HaloStatus status) {
        return new HaloResultV2<IdentifiedUser>(status, null);
    }
}
