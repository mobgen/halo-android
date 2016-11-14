package com.mobgen.halo.android.social.social.providers.facebook;

import android.os.Bundle;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.social.mock.instrumentation.StringShadowResources;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.social.providers.facebook.HaloFacebookSignInActivity;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import static com.mobgen.halo.android.social.mock.instrumentation.FacebookSocialProviderInstrument.givenAFacebookCancelledEvent;
import static com.mobgen.halo.android.social.mock.instrumentation.FacebookSocialProviderInstrument.givenAFacebookLoginErrorEvent;
import static com.mobgen.halo.android.social.mock.instrumentation.FacebookSocialProviderInstrument.givenAFacebookLoginSuccessEvent;


import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(shadows = StringShadowResources.class)
public class FacebookSocialProviderTest extends HaloRobolectricTest {

    @Override
    public void onStart() throws IOException, HaloParsingException {
    }

    @Override
    public void onDestroy() throws IOException {

    }

    @Test
    public void thatCanCreateFacebookProvider(){
        FacebookSocialProvider facebookSocialProvider = new FacebookSocialProvider();
        assertThat(facebookSocialProvider).isNotNull();
    }

    @Test
    public void thatHandleEventSuccessFromFacebookLoginActivity() throws IOException {
        FacebookSocialProvider facebookSocialProvider = new FacebookSocialProvider();
        facebookSocialProvider.onEventReceived(givenAFacebookLoginSuccessEvent());
        assertThat(facebookSocialProvider).isNotNull();
    }

    @Test
    public void thatHandleEventErrorFromFacebookLoginActivity() throws IOException {
        FacebookSocialProvider facebookSocialProvider = new FacebookSocialProvider();
        facebookSocialProvider.onEventReceived(givenAFacebookLoginErrorEvent());
        assertThat(facebookSocialProvider).isNotNull();
    }


    @Test
    public void thatHandleEventCancelledFromFacebookLoginActivity() throws IOException {
        FacebookSocialProvider facebookSocialProvider = new FacebookSocialProvider();
        facebookSocialProvider.onEventReceived(givenAFacebookCancelledEvent());
        assertThat(facebookSocialProvider).isNotNull();
    }

}
