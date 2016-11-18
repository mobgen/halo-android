package com.mobgen.halo.android.social.providers.google;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialProvider;
import com.mobgen.halo.android.social.providers.SocialProviderApi;

/**
 * The social provider for google plus.
 */
public class GoogleSocialProvider implements SocialProvider, Subscriber {
    /**
     * Name for social login with google plus.
     */
    public static final String SOCIAL_GOOGLE_NAME = "google";
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
    private CallbackV2<IdentifiedUser> userRequestCallbak;
    /**
     * The halo result profile.
     */
    private HaloResultV2<IdentifiedUser> haloSocialProfileHaloResult;
    /**
     * The google token
     */
    private String mGoogleToken;
    /**
     * The social provider api.
     */
    private SocialProviderApi mSocialProviderApi;

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
    public String getSocialNetworkName() {
        return SOCIAL_GOOGLE_NAME;
    }

    @Override
    public boolean linkedAppAvailable(@NonNull Context context) {
        return true; //No app is needed
    }

    @Override
    public void authenticate(final @NonNull Halo halo, @Nullable CallbackV2<IdentifiedUser> callback) {
        final Subscriber subscriber = this;
        mSocialProviderApi = SocialProviderApi.with(halo).build();
        userRequestCallbak = callback;
        if (mGoogleToken != null) {
            //we login user with previous credentials stored
            mSocialProviderApi.loginWithANetwork(getSocialNetworkName(), mGoogleToken).execute(new CallbackV2<IdentifiedUser>() {
                @Override
                public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                    if (result.status().isOk()) {
                        if (userRequestCallbak != null) {
                            userRequestCallbak.onFinish(result);
                        }
                        release();
                    } else { //We must revalidate token with google so we restart authentication proccess
                        launchGoogleActivity(halo, subscriber);
                    }
                }
            });
        } else {
            launchGoogleActivity(halo, subscriber);
        }
    }

    @Override
    public void setSocialToken(@NonNull String socialToken) {
        mGoogleToken = socialToken;
    }

    @Override
    public void setAuthProfile(@NonNull HaloAuthProfile haloAuthProfile) {
        return;
    }

    @Override
    public void release() {
        if (mAuthenticationSubscription != null) {
            mAuthenticationSubscription.unsubscribe();
            mAuthenticationSubscription = null;
        }
    }

    @Override
    public void onEventReceived(@NonNull Event event) {
        Bundle resultData = event.getData();
        if (resultData == null) {
            throw new IllegalStateException("The data provided by this sign in method should always bring a result.");
        }
        int loginResult = resultData.getInt(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_RESULT);
        haloSocialProfileHaloResult = null;
        if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_SUCCESS_CODE) { // Handle success
            success(resultData);
        } else if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_CANCELED_CODE) { // Handle cancel
            haloSocialProfileHaloResult = new HaloResultV2<>(HaloStatus.builder().cancel().build(), null);
        } else if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_ERROR_CODE) { //Handle error
            haloSocialProfileHaloResult = error(resultData);
        }
        emitResult();
    }

    /**
     * Launch Google Acitivity to request a token
     *
     * @param halo
     * @param subscriber
     */
    private void launchGoogleActivity(@NonNull Halo halo, @NonNull Subscriber subscriber) {
        //We must be login in just once
        if (mAuthenticationSubscription == null) {
            mAuthenticationSubscription = halo.framework().subscribe(subscriber, EventId.create(HaloGoogleSignInActivity.Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED));
            HaloGoogleSignInActivity.startActivity(halo.context(), mOptions);
        }
    }

    /**
     * Login user into halo with social credentials or notify process ended.
     */
    public void emitResult() {
        if (haloSocialProfileHaloResult == null && mSocialProviderApi != null) {
            mSocialProviderApi.loginWithANetwork(getSocialNetworkName(), mGoogleToken).execute(new CallbackV2<IdentifiedUser>() {
                @Override
                public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                    if (result.status().isOk()) {
                        if (userRequestCallbak != null) {
                            userRequestCallbak.onFinish(result);
                        }
                        release();
                    }
                }
            });
        } else if (userRequestCallbak != null) {
            userRequestCallbak.onFinish(haloSocialProfileHaloResult);
        }
        release();
    }

    /**
     * Generates the result for success.
     *
     * @param bundle The bundle with the result.
     * @return The result created.
     */
    @NonNull
    private void success(@NonNull Bundle bundle) {
        GoogleSignInAccount account = bundle.getParcelable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ACCOUNT);
        AssertionUtils.notNull(account, "account");
        mGoogleToken = account.getIdToken();
    }

    /**
     * Generates the error result.
     *
     * @param bundle The bundle containing the data.
     * @return The result.
     */
    @NonNull
    private HaloResultV2<IdentifiedUser> error(@NonNull Bundle bundle) {
        HaloIntegrationException error = (HaloIntegrationException) bundle.getSerializable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ERROR);
        HaloStatus status = HaloStatus.builder().error(error).build();
        return new HaloResultV2<>(status, null);
    }
}
