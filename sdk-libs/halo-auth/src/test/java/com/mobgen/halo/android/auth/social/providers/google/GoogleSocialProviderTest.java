package com.mobgen.halo.android.auth.social.providers.google;


import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mobgen.halo.android.auth.mock.instrumentation.StringShadowResources;
import com.mobgen.halo.android.auth.providers.google.GoogleSocialProvider;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.mobgen.halo.android.auth.mock.instrumentation.GoogleSocialProviderInstrument.givenAGoogleCancelledEvent;
import static com.mobgen.halo.android.auth.mock.instrumentation.GoogleSocialProviderInstrument.givenAGoogleLoginErrorEvent;
import static com.mobgen.halo.android.auth.mock.instrumentation.GoogleSocialProviderInstrument.givenAGoogleLoginSuccessEvent;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Config(shadows = StringShadowResources.class)
public class GoogleSocialProviderTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateGoogleProvider() {
        GoogleSignInOptions options = mock(GoogleSignInOptions.class);
        GoogleSocialProvider googleSocialProvider = new GoogleSocialProvider(options);
        assertThat(googleSocialProvider).isNotNull();
    }

    @Test
    public void thatHandleEventSuccessFromGoogleLoginActivity() throws IOException {
        GoogleSignInOptions options = mock(GoogleSignInOptions.class);
        GoogleSocialProvider googleSocialProvider = new GoogleSocialProvider(options);
        googleSocialProvider.onEventReceived(givenAGoogleLoginSuccessEvent());
        assertThat(googleSocialProvider).isNotNull();
    }

    @Test
    public void thatHandleEventErrorFromGoogleLoginActivity() throws IOException {
        GoogleSignInOptions options = mock(GoogleSignInOptions.class);
        GoogleSocialProvider googleSocialProvider = new GoogleSocialProvider(options);
        googleSocialProvider.onEventReceived(givenAGoogleLoginErrorEvent());
        assertThat(googleSocialProvider).isNotNull();
    }


    @Test
    public void thatHandleEventCancelledFromGoogleLoginActivity() throws IOException {
        GoogleSignInOptions options = mock(GoogleSignInOptions.class);
        GoogleSocialProvider googleSocialProvider = new GoogleSocialProvider(options);
        googleSocialProvider.onEventReceived(givenAGoogleCancelledEvent());
        assertThat(googleSocialProvider).isNotNull();
    }
}
