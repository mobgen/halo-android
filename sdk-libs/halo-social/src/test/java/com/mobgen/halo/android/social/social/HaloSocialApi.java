package com.mobgen.halo.android.social.social;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import static com.mobgen.halo.android.social.instrumentation.HaloSocialApiMock.givenASocialApi;

import java.io.IOException;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;


public class HaloSocialApi extends HaloRobolectricTest {

    private MockServer mMockServer;
    private Halo mHalo;
    private HaloSocialApi mHaloSocialApi;

    @Override
    public void onStart() throws IOException {
        mMockServer = MockServer.create();
       // mHalo = givenADefaultHalo(mMockServer.start());
       // mHaloSocialApi = givenASocialApi(mHalo);
       // mCallbackFlag = newCallbackFlag();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }
}
