package com.mobgen.halo.android.social.social.providers;


import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.exceptions.HaloConnectionException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetworkExceptionResolver;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.providers.SocialNotAvailableException;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.social.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiMock.givenASocialApiWithHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SocialNotAvailableExceptionTest extends HaloRobolectricTest {

    private Halo mHalo;
    private HaloSocialApi mHaloSocialApi;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("apiendpoint");
        mHaloSocialApi = givenASocialApiWithHalo(mHalo);
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatCreateANewSocialNotAvailableException() {
        SocialNotAvailableException socialException = new SocialNotAvailableException("Social provider not exist");
        assertThat(socialException).isNotNull();
        assertThat(socialException.getMessage()).contains("Social");
    }

    @Test
    public void thatCreateANewSocialNotAvailableExceptionWithErrorCausing() {
        SocialNotAvailableException socialException = new SocialNotAvailableException("Social provider not exist", new Exception());
        assertThat(socialException).isNotNull();
        assertThat(socialException.getMessage()).contains("Social");
    }

    @Test
    public void thatSocialNeExceptionIsThrow(){
        mHaloSocialApi.release();
        if(!mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_FACEBOOK)){
            try {
                mHaloSocialApi.login(HaloSocialApi.SOCIAL_FACEBOOK, new CallbackV2<HaloSocialProfile>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {

                    }
                });
            } catch (SocialNotAvailableException e) {
                assertThat(e).isNotNull();
                assertThat(e.getMessage()).contains("not available");
            }
        }
    }
}