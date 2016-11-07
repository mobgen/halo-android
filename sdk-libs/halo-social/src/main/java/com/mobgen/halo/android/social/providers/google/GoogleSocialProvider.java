package com.mobgen.halo.android.social.providers.google;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.authenticator.AuthTokenType;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialProvider;

/**
 * The social provider for google plus.
 */
public class GoogleSocialProvider implements SocialProvider, Subscriber {
    /**
     * Social api
     */
    private HaloSocialApi mSocialApi;
    /**
     * Name for social login with google plus.
     */
    public static final String SOCIAL_GOOGLE_PLUS_NAME = "google";
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
    private CallbackV2<HaloSocialProfile> mCallback;

    /**
     * The halo result profile.
     */
    private HaloResultV2<HaloSocialProfile> haloSocialProfileHaloResult;

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
        return SOCIAL_GOOGLE_PLUS_NAME;
    }

    @Override
    public boolean linkedAppAvailable(@NonNull Context context) {
        return true; //No app is needed
    }

    @Override
    public void authenticate(final @NonNull Halo halo, @NonNull String accountType, @NonNull CallbackV2<HaloSocialProfile> callback) {
        final Subscriber subscriber = this;
        mSocialApi = HaloSocialApi.with(Halo.instance())
                .storeCredentials(accountType)
                .withGoogle()
                .build();
        mCallback = callback;
        final String authSocialToken =  mSocialApi.recoverAuthToken(AuthTokenType.GOOGLE_AUTH_TOKEN);
        if(authSocialToken!=null){
            //we login user with previous credentials
            mSocialApi.loginWithANetwork(getSocialNetworkName(), authSocialToken)
                    .threadPolicy(Threading.SINGLE_QUEUE_POLICY)
                    .execute(new CallbackV2<IdentifiedUser>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<IdentifiedUser> resultIdentified) {
                            if(resultIdentified.status().isOk()) {
                                HaloSocialProfile profile = HaloSocialProfile.builder(authSocialToken)
                                        .socialName(getSocialNetworkName())
                                        .socialId(resultIdentified.data().getUser().getIdentifiedId())
                                        .name(resultIdentified.data().getUser().getName())
                                        .surname(resultIdentified.data().getUser().getSurname())
                                        .displayName(resultIdentified.data().getUser().getDisplayName())
                                        .email(resultIdentified.data().getUser().getEmail())
                                        .photo(resultIdentified.data().getUser().getPhoto())
                                        .build();
                                mCallback.onFinish(new HaloResultV2<>(resultIdentified.status(), profile));
                                release();
                            } else{ //We must revalidate token with google
                                if (mAuthenticationSubscription == null) {
                                    mAuthenticationSubscription = halo.framework().subscribe(subscriber, EventId.create(HaloGoogleSignInActivity.Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED));
                                    HaloGoogleSignInActivity.startActivity(halo.context(), mOptions);
                                }
                            }
                        }
                    });
        }else {
            //We must be login in just once
            if (mAuthenticationSubscription == null) {
                mAuthenticationSubscription = halo.framework().subscribe(this, EventId.create(HaloGoogleSignInActivity.Result.EVENT_NAME_GOOGLE_SIGN_IN_FINISHED));
                HaloGoogleSignInActivity.startActivity(halo.context(), mOptions);
            }
        }
    }

    @Override
    public void setAuthProfile(@NonNull HaloAuthProfile haloAuthProfile) {

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
            haloSocialProfileHaloResult = success(resultData);
        } else if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_CANCELED_CODE) { // Handle cancel
            haloSocialProfileHaloResult = new HaloResultV2<>(HaloStatus.builder().cancel().build(), null);
        } else if (loginResult == HaloGoogleSignInActivity.Result.GOOGLE_ERROR_CODE) { //Handle error
            haloSocialProfileHaloResult = error(resultData);
        }
        //Result must not be null, some error in configuration
        if (haloSocialProfileHaloResult == null) {
            String msg = "This should never happen. An event must have a GOOGLE_SIGN_IN_RESULT. Contact the halo support.";
            Halog.wtf(getClass(), msg);
            throw new IllegalStateException(msg);
        }

        //login user into halo with social credentials or notified process ended if we cant obtain social provider social token
        if(haloSocialProfileHaloResult.status().isOk() && haloSocialProfileHaloResult.data()!=null) {
            mSocialApi.loginWithANetwork(haloSocialProfileHaloResult.data().socialName(), haloSocialProfileHaloResult.data().socialToken())
                    .threadPolicy(Threading.SINGLE_QUEUE_POLICY)
                    .execute(new CallbackV2<IdentifiedUser>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<IdentifiedUser> resultIdentified) {
                            if(resultIdentified.status().isOk()) {
                                mCallback.onFinish(haloSocialProfileHaloResult);
                                release();
                            }
                        }
                    });
        }else{
            mCallback.onFinish(haloSocialProfileHaloResult);
            release();
        }

    }

    /**
     * Generates the result for success.
     *
     * @param bundle The bundle with the result.
     * @return The result created.
     */
    @NonNull
    @SuppressWarnings("all")
    private HaloResultV2<HaloSocialProfile> success(@NonNull Bundle bundle) {
        GoogleSignInAccount account = bundle.getParcelable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ACCOUNT);
        AssertionUtils.notNull(account, "account");
        String socialToken = account.getIdToken();
        AssertionUtils.notNull(socialToken, "socialToken");

        HaloSocialProfile profile = HaloSocialProfile.builder(socialToken)
                .socialName(getSocialNetworkName())
                .socialId(account.getId())
                .name(account.getGivenName())
                .surname(account.getFamilyName())
                .displayName(account.getDisplayName())
                .email(account.getEmail())
                .photo(account.getPhotoUrl().toString())
                .build();
        return new HaloResultV2<>(HaloStatus.builder().build(), profile);
    }

    /**
     * Generates the error result.
     *
     * @param bundle The bundle containing the data.
     * @return The result.
     */
    @NonNull
    private HaloResultV2<HaloSocialProfile> error(@NonNull Bundle bundle) {
        HaloIntegrationException error = (HaloIntegrationException) bundle.getSerializable(HaloGoogleSignInActivity.Result.GOOGLE_SIGN_IN_ERROR);
        HaloStatus status = HaloStatus.builder().error(error).build();
        return new HaloResultV2<>(status, null);
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
