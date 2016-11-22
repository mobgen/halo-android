package com.mobgen.halo.android.social.mock.instrumentation;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;

public class HaloSocialApiMock {

    public static HaloSocialApi givenASocialApiWithAllNetworksAvailable(Halo halo) {
        return HaloSocialApi.with(halo)
                .storeCredentials("halo.account.manager")
                .recoveryPolicy(HaloSocialApi.RECOVERY_ALWAYS)
                .withGoogle()
                .withFacebook()
                .withHalo()
                .build();
    }

    public static HaloSocialApi givenASocialApiWithHalo(Halo halo) {
        return HaloSocialApi.with(halo)
                .storeCredentials("halo.account.manager")
                .withFacebook()
                .build();
    }

    public static HaloSocialApi givenASocialApiWithGoogle(Halo halo) {
        return HaloSocialApi.with(halo)
                .storeCredentials("halo.account.manager")
                .withFacebook()
                .build();
    }

    public static HaloSocialApi givenASocialApiWithFacebook(Halo halo) {
        return HaloSocialApi.with(halo)
                .storeCredentials("halo.account.manager")
                .withFacebook()
                .build();
    }
}
