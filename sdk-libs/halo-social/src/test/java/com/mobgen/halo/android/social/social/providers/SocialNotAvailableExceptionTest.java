package com.mobgen.halo.android.social.social.providers;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialNotAvailableException;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.social.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiMock.givenASocialApiWithHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SocialNotAvailableExceptionTest extends HaloRobolectricTest {

    private Halo mHalo;
    private HaloSocialApi mHaloSocialApi;
    private Boolean flag;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("apiendpoint");
        mHaloSocialApi = givenASocialApiWithHalo(mHalo);
        flag = false;
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

    @Test(expected = SocialNotAvailableException.class)
    public void thatSocialNeExceptionIsThrow() throws SocialNotAvailableException {
        mHaloSocialApi.release();
        if (!mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_FACEBOOK)) {
            mHaloSocialApi.loginWithSocial(HaloSocialApi.SOCIAL_FACEBOOK, new CallbackV2<IdentifiedUser>() {
                @Override
                public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                    flag = true;
                }
            });
        }
        assertThat(flag).isFalse();
    }
}