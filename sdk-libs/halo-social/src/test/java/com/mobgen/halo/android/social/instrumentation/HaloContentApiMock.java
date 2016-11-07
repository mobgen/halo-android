package com.mobgen.halo.android.content.mock.instrumentation;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;

public class HaloContentApiMock {

    public static HaloSocialApi givenASocialApi(Halo halo) {
        return HaloSocialApi.with(halo)
                .accountType("testaccount")
                .withHalo()
                .withFacebook()
                .withGoogle()
                .build();
    }
}
