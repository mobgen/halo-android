package com.mobgen.halo.android.social.providers.facebook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.FacebookSdk;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
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
 * The social provider for facebook authentication.
 */
public class FacebookSocialProvider implements SocialProvider, Subscriber {
    /**
     * Social api
     */
    private HaloSocialApi mSocialApi;
    /**
     * Name for social login with facebook.
     */
    public static final String SOCIAL_FACEBOOK_NAME = "facebook";
    /**
     * Pending callback for the facebook login.
     */
    private CallbackV2<HaloSocialProfile> mPendingCallbackResolution;
    /**
     * The halo result profile.
     */
    private HaloResultV2<HaloSocialProfile> haloSocialProfileHaloResult;
    /**
     * Subscription for the facebook login.
     */
    private ISubscription mFacebookSubscription;

    @Override
    public boolean isLibraryAvailable(@NonNull Context context) {
        try {
            Class.forName("com.facebook.FacebookActivity");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getSocialNetworkName() {
        return SOCIAL_FACEBOOK_NAME;
    }

    @Override
    public boolean linkedAppAvailable(@NonNull Context context) {
        return true;
    }

    @Override
    public void authenticate(final @NonNull Halo halo, @NonNull String accountType, @NonNull CallbackV2<HaloSocialProfile> callback) {
        final Subscriber subscriber = this;
        initIfNeeded(halo.context());
        mSocialApi = (HaloSocialApi)halo.manager().haloSocial();
        final String authSocialToken =  mSocialApi.recoverAuthToken(AuthTokenType.FACEBOOK_AUTH_TOKEN);
        mPendingCallbackResolution = callback;
        if(authSocialToken!=null && mSocialApi.getRecoveryPolicy()!=HaloSocialApi.RECOVERY_ALWAYS) {
            mSocialApi.loginWithANetwork(getSocialNetworkName() , authSocialToken)
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
                                //Finish the callback
                                mPendingCallbackResolution.onFinish(new HaloResultV2<>(resultIdentified.status(), profile));
                                //Release the login resources
                                release();
                            } else {  //We must revalidate token with facebook
                                mFacebookSubscription = halo.framework().subscribe(subscriber, EventId.create(HaloFacebookSignInActivity.Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED));
                                HaloFacebookSignInActivity.startActivity(halo.context());
                            }
                        }
                    });
        } else {
            mFacebookSubscription = halo.framework().subscribe(this, EventId.create(HaloFacebookSignInActivity.Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED));
            HaloFacebookSignInActivity.startActivity(halo.context());
        }
    }

    @Override
    public void setAuthProfile(@NonNull HaloAuthProfile haloAuthProfile) {

    }

    @Override
    public void release() {
        if (mFacebookSubscription != null) {
            mFacebookSubscription.unsubscribe();
            mFacebookSubscription = null;
        }
    }

    /**
     * Initializes the facebook sdk if needed.
     *
     * @param context The context.
     */
    private void initIfNeeded(@NonNull Context context) {
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(context);
        }
    }

    @Override
    public void onEventReceived(@NonNull Event event) {
        Bundle resultData = event.getData();
        if (resultData == null) {
            throw new IllegalStateException("The data provided by this sign in method should always bring a result.");
        }
        int loginEventResult = resultData.getInt(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_RESULT);
        haloSocialProfileHaloResult = null;
        if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_SUCCESS_CODE) { //Success
            haloSocialProfileHaloResult = success(resultData);
        } else if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_CANCELED_CODE) { //Cancelled
            haloSocialProfileHaloResult = new HaloResultV2<>(HaloStatus.builder().cancel().build(), null);
        } else if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_ERROR_CODE) { // Error
            haloSocialProfileHaloResult = error(resultData);
        }

        //Result must not be null, some error in configuration
        if (haloSocialProfileHaloResult == null) {
            String msg = "This should never happen. An event must have a FACEBOOK_SIGN_IN_RESULT. Contact the halo support.";
            Halog.wtf(getClass(), msg);
            throw new IllegalStateException(msg);
        }
        //login user into halo with social credentials or notified process ended if we cant obtain social provider social token
        if(haloSocialProfileHaloResult.status().isOk() && haloSocialProfileHaloResult.data()!=null){
            mSocialApi.loginWithANetwork(haloSocialProfileHaloResult.data().socialName(), haloSocialProfileHaloResult.data().socialToken())
                    .threadPolicy(Threading.SINGLE_QUEUE_POLICY)
                    .execute(new CallbackV2<IdentifiedUser>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<IdentifiedUser> resultIdentified) {
                            if(resultIdentified.status().isOk()) {
                                //Finish the callback
                                mPendingCallbackResolution.onFinish(haloSocialProfileHaloResult);
                                //Release the login resources
                                release();
                            }
                        }
                    });
        }else {
            //Finish the callback
            mPendingCallbackResolution.onFinish(haloSocialProfileHaloResult);
            //Release the login resources
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
    private HaloResultV2<HaloSocialProfile> success(@NonNull Bundle bundle) {
        HaloSocialProfile profile = bundle.getParcelable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ACCOUNT);
        profile.setSocialName(getSocialNetworkName());
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
        HaloIntegrationException e = (HaloIntegrationException) bundle.getSerializable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ERROR);
        HaloStatus status = HaloStatus.builder()
                .error(new HaloIntegrationException("Error integrating the facebook login", e))
                .build();
        return new HaloResultV2<>(status, null);
    }
}