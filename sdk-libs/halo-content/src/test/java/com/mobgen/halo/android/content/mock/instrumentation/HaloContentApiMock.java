package com.mobgen.halo.android.content.mock.instrumentation;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.sdk.api.Halo;

public class HaloContentApiMock {

    public static HaloContentApi givenAContentApi(Halo halo) {
        return HaloContentApi.with(halo);
    }
}
