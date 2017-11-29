package com.mobgen.halo.android.auth.mock.instrumentation;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.auth.HaloAuthApi;

public class HaloSocialApiMock {

    public static HaloAuthApi givenASocialApiWithAllNetworksAvailable(Halo halo) {
        return HaloAuthApi.with(halo)
                .storeCredentials("halo.account.manager")
                .recoveryPolicy(HaloAuthApi.RECOVERY_ALWAYS)
                .withGoogle()
                .withFacebook()
                .withHalo()
                .build();
    }

    public static HaloAuthApi givenASocialApiWithHalo(Halo halo) {
        return HaloAuthApi.with(halo)
                .storeCredentials("halo.account.manager")
                .withFacebook()
                .build();
    }

    public static HaloAuthApi givenASocialApiWithGoogle(Halo halo) {
        return HaloAuthApi.with(halo)
                .storeCredentials("halo.account.manager")
                .withFacebook()
                .build();
    }

    public static HaloAuthApi givenASocialApiWithFacebook(Halo halo) {
        return HaloAuthApi.with(halo)
                .storeCredentials("halo.account.manager")
                .withFacebook()
                .build();
    }

    public static HaloAuthApi givenASocialApiWithoutRecover(Halo halo) {
        return HaloAuthApi.with(halo)
                .withHalo()
                .build();
    }
}
