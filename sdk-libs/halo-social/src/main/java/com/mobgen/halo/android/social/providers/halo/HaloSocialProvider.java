package com.mobgen.halo.android.social.providers.halo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloAuthenticationException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.login.LoginInteractor;
import com.mobgen.halo.android.social.login.LoginRemoteDatasource;
import com.mobgen.halo.android.social.login.LoginRepository;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialProvider;

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
     * The auth profile
     */
    private HaloAuthProfile mHaloAuthProfile;

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
    public void setAuthProfile(@Nullable HaloAuthProfile haloAuthProfile) {
        mHaloAuthProfile = haloAuthProfile;
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
    public void authenticate(@NonNull Halo halo, @NonNull CallbackV2<IdentifiedUser> callback) {
        userRequestCallbak = callback;
        if (mHaloAuthProfile != null) {
            loginWithHalo(mHaloAuthProfile.getEmail(), mHaloAuthProfile.getPassword())
                    .execute(new CallbackV2<IdentifiedUser>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                            if (result.status().isOk()) {
                                userRequestCallbak.onFinish(result);
                            } else {
                                userRequestCallbak.onFinish(error(result.status()));
                            }
                        }
                    });
        } else {
            //we dont have user credential to login
            HaloStatus.Builder status = HaloStatus.builder();
            status.error(new HaloAuthenticationException("Error"));
            userRequestCallbak.onFinish(error(status.build()));
        }
    }

    private HaloResultV2<IdentifiedUser> error(HaloStatus status) {
        return new HaloResultV2<IdentifiedUser>(status, null);
    }

    /**
     * Tries to login with halo
     *
     * @param username The social network to login with.
     * @param password The social token
     */
    @NonNull
    public HaloInteractorExecutor<IdentifiedUser> loginWithHalo(@NonNull String username, @NonNull String password) {
        AssertionUtils.notNull(username, "username");
        AssertionUtils.notNull(password, "password");
        HaloSocialApi socialApi = (HaloSocialApi) Halo.instance().manager().haloSocial();
        return new HaloInteractorExecutor<>(Halo.instance(),
                "Login with halo",
                new LoginInteractor(socialApi.accountType(), new LoginRepository(new LoginRemoteDatasource(Halo.instance().framework().network())),
                        username, password, Halo.instance().manager().getDevice().getAlias(), socialApi.recoveryPolicy())
        );
    }
}
