package com.mobgen.halo.android.social.google;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.sync.bus.Event;
import com.mobgen.halo.android.framework.sync.bus.EventId;
import com.mobgen.halo.android.framework.sync.bus.Subscriber;
import com.mobgen.halo.android.framework.sync.callbacks.Callback;
import com.mobgen.halo.android.framework.sync.response.HaloResult;
import com.mobgen.halo.android.framework.sync.response.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialProfile;
import com.mobgen.halo.android.social.SocialProvider;

/**
 * The social provider for google plus.
 */
public class GoogleSocialProvider implements SocialProvider, Subscriber {

    /**
     * Sign in options for google plus.
     */
    private GoogleSignInOptions mOptions;
    /**
     * Event subscription to listen for the login provider.
     */
    private ISubscription mAuthenticationSubscription;
    /**
     * The callback to provide the result.
     */
    private Callback<HaloSocialProfile, Void> mCallback;

    /**
     * Constructor for the social provider for google plus.
     *
     * @param options The options.
     */
    public GoogleSocialProvider(@NonNull GoogleSignInOptions options) {
        AssertionUtils.notNull(options, "options");
        mOptions = options;
    }

    @Override
    public boolean isLibraryAvailable(@NonNull Context context) {
        try {
            Class.forName("com.google.android.gms.auth.api.Auth");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean linkedAppAvailable(@NonNull Context context) {
        return true; //No app is needed
    }

    @Override
    public void authenticate(@NonNull Halo halo, @NonNull Callback<HaloSocialProfile, Void> callback) {
        //We must be login in just once
        if (mAuthenticationSubscription == null) {
            mCallback = callback;
            mAuthenticationSubscription = halo.framework().subscribe(this, EventId.create(HaloGoogleSignInActivity.Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED));
            HaloGoogleSignInActivity.startActivity(halo.context(), mOptions);
        }
    }

    @Override
    public void onEventReceived(@NonNull Event event) {
        Bundle resultData = event.getData();
        if (resultData == null) {
            throw new IllegalStateException("The data provided by this sign in method should always bring a result.");
        }
        int loginResult = resultData.getInt(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_RESULT);
        HaloResult<HaloSocialProfile, Void> result = null;
        if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_SUCCESS_CODE) { // Handle success
            result = success(resultData);
        } else if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_CANCELED_CODE) { // Handle cancel
            result = new HaloResult<>(HaloStatus.builder().cancel().build(), null);
        } else if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_ERROR_CODE) { //Handle error
            result = error(resultData);
        }
        //Result must not be null, some error in configuration
        if (result == null) {
            String msg = "This should never happen. An event must have a GOOGLE_SIGN_IN_RESULT. Contact the halo support.";
            Halog.wtf(getClass(), msg);
            throw new IllegalStateException(msg);
        }
        mCallback.onFinish(result);
        release();
    }

    /**
     * Generates the result for success.
     *
     * @param bundle The bundle with the result.
     * @return The result created.
     */
    @NonNull
    @SuppressWarnings("all")
    private HaloResult<HaloSocialProfile, Void> success(@NonNull Bundle bundle) {
        GoogleSignInAccount account = bundle.getParcelable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ACCOUNT);
        AssertionUtils.notNull(account, "account");
        String socialToken = account.getIdToken();
        AssertionUtils.notNull(socialToken, "socialToken");
        HaloSocialProfile profile = HaloSocialProfile.builder(socialToken)
                .socialId(account.getId())
                .name(account.getGivenName())
                .surname(account.getFamilyName())
                .displayName(account.getDisplayName())
                .email(account.getEmail())
                .photo(account.getPhotoUrl())
                .build();
        return new HaloResult<>(HaloStatus.builder().build(), profile);
    }

    /**
     * Generates the error result.
     *
     * @param bundle The bundle containing the data.
     * @return The result.
     */
    @NonNull
    private HaloResult<HaloSocialProfile, Void> error(@NonNull Bundle bundle) {
        HaloIntegrationException error = (HaloIntegrationException) bundle.getSerializable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ERROR);
        HaloStatus status = HaloStatus.builder().error(error).build();
        return new HaloResult<>(status, null);
    }

    @Override
    public void release() {
        if (mAuthenticationSubscription != null) {
            mAuthenticationSubscription.unsubscribe();
            mAuthenticationSubscription = null;
        }
        mCallback = null;
    }
}
