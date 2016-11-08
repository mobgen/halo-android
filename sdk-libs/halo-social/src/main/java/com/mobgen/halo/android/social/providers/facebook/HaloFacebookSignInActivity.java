package com.mobgen.halo.android.social.providers.facebook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloConnectionException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloSocialProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

/**
 * Sign in shadow activity to handle the sign in process.
 */
public class HaloFacebookSignInActivity extends FragmentActivity implements FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {

    /**
     * The callback manager
     */
    private CallbackManager mCallbackManager;
    /**
     * Tells that an event has been emitted.
     */
    private boolean mEmitted;
    /**
     * The access token.
     */
    private AccessToken mAccessToken;
    /**
     * The result that must be reported.
     */
    public static class Result {

        //Events
        /**
         * Event emitted when the sign in process has started.
         */
        public static final String EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED = "halo_facebook_sign_in_event_name";

        //Params
        /**
         * The result param for the event emitted in a sign in attempt. This param will contain
         * an integer which means {@link #FACEBOOK_SUCCESS_CODE} or {@link #FACEBOOK_ERROR_CODE}.
         */
        public static final String FACEBOOK_SIGN_IN_RESULT = "halo_facebook_sign_in_result";

        /**
         * Provides the signed in account as a parceled object if the {@link #FACEBOOK_SIGN_IN_RESULT} is {@link #FACEBOOK_SUCCESS_CODE}.
         */
        public static final String FACEBOOK_SIGN_IN_ACCOUNT = "halo_facebook_sign_in_account";

        /**
         * This param contains the exception as a serialized object in the event bundle {@link #EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED}
         * with the data if the param {@link #FACEBOOK_SIGN_IN_RESULT} contains the code {@link #FACEBOOK_ERROR_CODE}.
         */
        public static final String FACEBOOK_SIGN_IN_ERROR = "halo_facebook_sign_in_error";

        //Codes
        /**
         * Success when login in with the current param.
         */
        public static final int FACEBOOK_SUCCESS_CODE = 0;

        /**
         * The sign in has been canceled.
         */
        public static final int FACEBOOK_CANCELED_CODE = 1;

        /**
         * Error while login in with the current param.
         */
        public static final int FACEBOOK_ERROR_CODE = 2;
    }

    /**
     * Starts the google sign in activity that is transparent to start login in.
     *
     * @param context The context for the application.
     */
    public static void startActivity(@NonNull Context context) {
        AssertionUtils.notNull(context, "context");
        Intent intent = new Intent(context, HaloFacebookSignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (HaloUtils.isNetworkConnected(this)) {
            loginAttempt();
        } else {
            onError(new HaloConnectionException("Network is not available", null), HaloSocialApi.Error.CODE_NO_INTERNET);
        }
    }

    private void loginAttempt() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager manager = LoginManager.getInstance();
        manager.registerCallback(mCallbackManager, this);
        manager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallbackManager = null;
        //If the event was not emitted just emit a cancellation
        if (!mEmitted) {
            onCancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        mAccessToken = loginResult.getAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(mAccessToken, this);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,middle_name,last_name,link,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        Bundle params = new Bundle();
        params.putInt(Result.FACEBOOK_SIGN_IN_RESULT, Result.FACEBOOK_CANCELED_CODE);
        finishAndEmit(new Event(EventId.create(Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED), params));
    }

    @Override
    public void onError(@NonNull FacebookException e) {
        if (isPermissionRevoked(e)) {
            //Try to login again in case of a revoked permission
            AccessToken.setCurrentAccessToken(null);
            loginAttempt();
        } else if (isNetworkError(e)) {
            onError(e, HaloSocialApi.Error.CODE_NO_INTERNET);
        } else {
            onError(e, HaloSocialApi.Error.CODE_PROVIDER_ERROR);
        }
    }

    @Override
    public void onCompleted(@Nullable JSONObject object, @NonNull GraphResponse response) {
        if (response.getError() != null) {
            onError(response.getError().getException());
        } else if (object != null) { //Process the data
            try {
                String id = object.getString("id");
                Uri photo = new Uri.Builder().scheme("https").authority("graph.facebook.com").path(String.format(Locale.US, "%s/picture", new Object[]{id})).build();
                emitProfile(HaloSocialProfile.builder(mAccessToken.getToken())
                        .socialId(id)
                        .displayName(object.getString("name"))
                        .name(object.getString("first_name"))
                        .surname(object.getString("last_name"))
                        .email(object.getString("email"))
                        .photo(photo.toString())
                        .build());
            } catch (JSONException e) {
                onError(e, HaloSocialApi.Error.CODE_NO_INTERNET);
            }
        } else {
            onError(new IllegalStateException("Something wrong happened with the facebook request. No data available"), HaloSocialApi.Error.CODE_NO_INTERNET);
        }
    }

    @Override
    public void onBackPressed() {
        //Avoid back press to close this activity
    }

    /**
     * Checks if the facebook error is a network exception.
     *
     * @param e The exception.
     * @return True if it is a network error. False otherwise.
     */
    private boolean isNetworkError(@NonNull FacebookException e) {
        return "net::ERR_INTERNET_DISCONNECTED".equals(e.getMessage());
    }

    /**
     * Handles the exceptions thrown during this integration.
     *
     * @param e The exception.
     */
    private void onError(Exception e, int errorCode) {
        Bundle params = new Bundle();
        HaloIntegrationException integrationException = new HaloIntegrationException("Social network provider error", errorCode, "Error integrating the facebook login", e);
        params.putInt(Result.FACEBOOK_SIGN_IN_RESULT, Result.FACEBOOK_ERROR_CODE);
        params.putSerializable(Result.FACEBOOK_SIGN_IN_ERROR, integrationException);
        finishAndEmit(new Event(EventId.create(Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED), params));
    }

    /**
     * Emits the facebook profile.
     *
     * @param profile The facebook profile.
     */
    private void emitProfile(@NonNull HaloSocialProfile profile) {
        Bundle params = new Bundle();
        params.putInt(Result.FACEBOOK_SIGN_IN_RESULT, Result.FACEBOOK_SUCCESS_CODE);
        params.putParcelable(Result.FACEBOOK_SIGN_IN_ACCOUNT, profile);
        finishAndEmit(new Event(EventId.create(Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED), params));
    }

    /**
     * Finishes this activity and emits the event provided.
     * @param event The event to emit.
     */
    private void finishAndEmit(@NonNull Event event) {
        if (!mEmitted) { //Just one emission is allowed per instance
            mEmitted = true;
            finish();
            Halo.instance().framework().emit(event);
        }
    }

    /**
     * Checks if the access token can be used for other purposes.
     *
     * @param e The exception.
     * @return True if it is not usable. False otherwise.
     */
    private boolean isPermissionRevoked(@NonNull FacebookException e) {
        return e.getMessage().contains("errorCode: 190");
    }
}
