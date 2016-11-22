package com.mobgen.halo.android.social.social.providers.facebook;

import com.mobgen.halo.android.social.mock.instrumentation.StringShadowResources;
import com.mobgen.halo.android.social.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.mobgen.halo.android.social.mock.instrumentation.FacebookSocialProviderInstrument.givenAFacebookCancelledEvent;
import static com.mobgen.halo.android.social.mock.instrumentation.FacebookSocialProviderInstrument.givenAFacebookLoginErrorEvent;
import static com.mobgen.halo.android.social.mock.instrumentation.FacebookSocialProviderInstrument.givenAFacebookLoginSuccessEvent;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(shadows = StringShadowResources.class)
public class FacebookSocialProviderTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateFacebookProvider() {
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
