package com.mobgen.halo.android.social.mock.instrumentation;


import android.os.Bundle;

import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.providers.facebook.HaloFacebookSignInActivity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacebookSocialProviderInstrument {

    public static Event givenAFacebookLoginSuccessEvent(){
        Event event = mock(Event.class);
        Bundle bundle = new Bundle();
        bundle.putInt(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_RESULT,HaloFacebookSignInActivity.Result.FACEBOOK_SUCCESS_CODE);
        HaloSocialProfile haloSocialProfile = mock(HaloSocialProfile.class);
        bundle.putParcelable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ACCOUNT,haloSocialProfile);
        when(event.getData()).thenReturn(bundle);
        return event;
    }

    public static Event givenAFacebookLoginErrorEvent(){
        Event event = mock(Event.class);
        Bundle bundle = new Bundle();
        bundle.putInt(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_RESULT,HaloFacebookSignInActivity.Result.FACEBOOK_ERROR_CODE);
        HaloSocialProfile haloSocialProfile = mock(HaloSocialProfile.class);
        bundle.putParcelable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ACCOUNT,haloSocialProfile);
        when(event.getData()).thenReturn(bundle);
        return event;
    }

    public static Event givenAFacebookCancelledEvent() {
        Event event = mock(Event.class);
        Bundle bundle = new Bundle();
        bundle.putInt(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_RESULT,HaloFacebookSignInActivity.Result.FACEBOOK_CANCELED_CODE);
        HaloSocialProfile haloSocialProfile = mock(HaloSocialProfile.class);
        bundle.putParcelable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ACCOUNT,haloSocialProfile);
        when(event.getData()).thenReturn(bundle);
        return event;
    }
}
