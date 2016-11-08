package com.mobgen.halo.android.social.instrumentation;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;

public class HaloSocialApiMock {

    public static HaloSocialApi givenASocialApi(Halo halo) {
        return HaloSocialApi.with(halo)
                .storeCredentials("testaccount")
                .withHalo()
                .withFacebook()
                .withGoogle()
                .build();
    }
}
