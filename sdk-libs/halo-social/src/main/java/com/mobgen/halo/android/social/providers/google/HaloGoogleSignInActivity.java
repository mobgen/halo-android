package com.mobgen.halo.android.social.providers.google;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloConnectionException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;

/**
 * Shadow activity that helps in the action of login in a current user for the given application.
 */
public class HaloGoogleSignInActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    /**
     * Google sign in options param.
     */
    private static final String GOOGLE_SIGN_IN_OPTIONS = "google_client_options";

    /**
     * Sign in request.
     */
    private static final int SIGN_IN_REQUEST_CODE = 1;

    /**
     * Google apis connection client.
     */
    private GoogleApiClient mGoogleClient;

    /**
     * The result that must be reported.
     */
    public static class Result {

        //Events
        /**
         * Event emitted when the sign in process has started.
         */
        public static final String EVENT_NAME_GOOGLE_SIGN_IN_FINISHED = "halo_google_sign_in_event_name";

        //Params
        /**
         * The result param for the event emitted in a sign in attempt. This param will contain
         * an integer which means {@link #GOOGLE_SUCCESS_CODE} or {@link #GOOGLE_ERROR_CODE}.
         */
        public static final String GOOGLE_SIGN_IN_RESULT = "halo_google_sign_in_result";

        /**
         * Provides the signed in account as a parceled object if the {@link #GOOGLE_SIGN_IN_RESULT} is {@link #GOOGLE_SUCCESS_CODE}.
         */
        public static final String GOOGLE_SIGN_IN_ACCOUNT = "halo_google_sign_in_account";

        /**
         * This param contains the exception as a serialized object in the event bundle {@link #EVENT_NAME_GOOGLE_SIGN_IN_FINISHED}
         * with the data if the param {@link #GOOGLE_SIGN_IN_RESULT} contains the code {@link #GOOGLE_ERROR_CODE}.
         */
        public static final String GOOGLE_SIGN_IN_ERROR = "halo_google_sign_in_error";

        //Codes
        /**
         * Success when login in with the current param.
         */
        public static final int GOOGLE_SUCCESS_CODE = 0;

        /**
         * The sign in has been canceled.
         */
        public static final int GOOGLE_CANCELED_CODE = 1;

        /**
         * Error while login in with the current param.
         */
        public static final int GOOGLE_ERROR_CODE = 2;

        /**
         * The status code.
         */
        public static int GOOGLE_CANCEL_ERROR_CODE = 12501;
    }

    /**
     * Starts the google sign in activity that is transparent to start login in.
     *
     * @param context The context for the application.
     * @param options The options for the sign in.
     */
    public static void startActivity(@NonNull Context context, @NonNull GoogleSignInOptions options) {
        AssertionUtils.notNull(context, "context");
        AssertionUtils.notNull(options, "options");
        Intent intent = new Intent(context, HaloGoogleSignInActivity.class);
        intent.putExtra(GOOGLE_SIGN_IN_OPTIONS, options);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (HaloUtils.isNetworkConnected(this)) {
            if (savedInstanceState == null) {
                GoogleSignInOptions options = getIntent().getExtras().getParcelable(GOOGLE_SIGN_IN_OPTIONS);
                if (options == null) {
                    throw new IllegalStateException("The options provided must not be null or lost in state.");
                }
                mGoogleClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                        .addConnectionCallbacks(this)
                        .build();
            }
        } else {
            onError(new HaloConnectionException("Network is not available", null), HaloSocialApi.Error.CODE_NO_INTERNET);
        }
    }

    /**
     * Starts the sign in process.
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleClient);
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        signIn();
    }

    @Override
    public void onConnectionSuspended(int suspensionCode) {
        //Nothing here
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        HaloIntegrationException error = new HaloIntegrationException(connectionResult.getErrorMessage(), connectionResult.getErrorCode(), "Connection lost", null);
        Bundle params = new Bundle();
        params.putInt(Result.GOOGLE_SIGN_IN_RESULT, Result.GOOGLE_ERROR_CODE);
        params.putSerializable(Result.GOOGLE_SIGN_IN_ERROR, error);
        finish();
        Halo.instance().framework().emit(new Event(EventId.create(Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED), params));
    }

    /**
     * Handles the sign in result.
     *
     * @param result The result received.
     */
    private void handleSignInResult(@NonNull GoogleSignInResult result) {
        Bundle params = new Bundle();
        if (result.isSuccess()) { //Success
            // Signed in successfully, report authenticated in an event
            GoogleSignInAccount account = result.getSignInAccount();
            params.putParcelable(Result.GOOGLE_SIGN_IN_ACCOUNT, account);
            params.putInt(Result.GOOGLE_SIGN_IN_RESULT, Result.GOOGLE_SUCCESS_CODE);
            finishAndEmit(new Event(EventId.create(Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED), params));
        } else if (result.getStatus().isCanceled() || result.getStatus().isInterrupted()) { // Cancelled
            params.putInt(Result.GOOGLE_SIGN_IN_RESULT, Result.GOOGLE_CANCELED_CODE);
            finishAndEmit(new Event(EventId.create(Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED), params));
        } else { //Error
            onError(new GoogleAuthException(result.getStatus().toString()), HaloSocialApi.Error.CODE_PROVIDER_ERROR);
        }
    }

    /**
     * Handles the exceptions thrown during this integration.
     *
     * @param e The exception.
     */
    private void onError(Exception e, int errorCode) {
        Bundle params = new Bundle();
        HaloIntegrationException integrationException = new HaloIntegrationException("Social network provider error", errorCode, "Error integrating the google login", e);
        params.putInt(Result.GOOGLE_SIGN_IN_RESULT, Result.GOOGLE_ERROR_CODE);
        params.putSerializable(Result.GOOGLE_SIGN_IN_ERROR, integrationException);
        finishAndEmit(new Event(EventId.create(Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED), params));
    }

    /**
     * Finishes this activity and emits the event provided.
     *
     * @param event The event to emit.
     */
    private void finishAndEmit(@NonNull Event event) {
        finish();
        Halo.instance().framework().emit(event);
    }
}
