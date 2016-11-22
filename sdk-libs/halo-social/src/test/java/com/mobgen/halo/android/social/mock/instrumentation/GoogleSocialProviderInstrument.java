package com.mobgen.halo.android.social.mock.instrumentation;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.social.providers.google.HaloGoogleSignInActivity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleSocialProviderInstrument {

    public static Event givenAGoogleLoginSuccessEvent(){
        Event event = mock(Event.class);
        Bundle bundle = new Bundle();
        bundle.putInt(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_RESULT,HaloGoogleSignInActivity.Result.GOOGLE_SUCCESS_CODE);
        GoogleSignInAccount googleSignInAccount = mock(GoogleSignInAccount.class);
        bundle.putParcelable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ACCOUNT,googleSignInAccount);
        when(googleSignInAccount.getIdToken()).thenReturn("mygoogletoken");
        when(googleSignInAccount.getPhotoUrl()).thenReturn(Uri.parse("http://photourl.com"));
        when(event.getData()).thenReturn(bundle);
        return event;
    }

    public static Event givenAGoogleLoginErrorEvent(){
        Event event = mock(Event.class);
        Bundle bundle = new Bundle();
        bundle.putInt(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_RESULT,HaloGoogleSignInActivity.Result.GOOGLE_ERROR_CODE);
        bundle.putSerializable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ERROR,new HaloIntegrationException("HaloIntegrationException",new Exception()));
        when(event.getData()).thenReturn(bundle);
        return event;
    }

    public static Event givenAGoogleCancelledEvent() {
        Event event = mock(Event.class);
        Bundle bundle = new Bundle();
        bundle.putInt(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_RESULT,HaloGoogleSignInActivity.Result.GOOGLE_CANCELED_CODE);
        when(event.getData()).thenReturn(bundle);
        return event;
    }
}
