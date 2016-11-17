package com.mobgen.halo.android.social.providers.facebook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.FacebookSdk;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.login.LoginRemoteDatasource;
import com.mobgen.halo.android.social.login.LoginRepository;
import com.mobgen.halo.android.social.login.SocialLoginInteractor;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialProvider;

/**
 * The social provider for facebook authentication.
 */
public class FacebookSocialProvider implements SocialProvider, Subscriber {
    /**
     * Name for social login with facebook.
     */
    public static final String SOCIAL_FACEBOOK_NAME = "facebook";
    /**
     * Pending callback for the facebook login.
     */
    private CallbackV2<IdentifiedUser> userRequestCallbak;
    /**
     * The halo result profile.
     */
    private HaloResultV2<IdentifiedUser> haloSocialProfileHaloResult;
    /**
     * Subscription for the facebook login.
     */
    private ISubscription mFacebookSubscription;
    /**
     * The facebook token
     */
    private String mFacebookToken;

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
    public void authenticate(final @NonNull Halo halo, @NonNull CallbackV2<IdentifiedUser> callback) {
        final Subscriber subscriber = this;
        initIfNeeded(halo.context());
        userRequestCallbak = callback;
        if (mFacebookToken != null) {
            loginWithANetwork().execute(new CallbackV2<IdentifiedUser>() {
                @Override
                public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                    if (result.status().isOk()) {
                        //Finish the callback
                        userRequestCallbak.onFinish(result);
                        //Release the login resources
                        release();
                    } else {//We must revalidate token with facebook
                        launchFacebookActivity(halo, subscriber);
                    }
                }
            });
        } else {
            launchFacebookActivity(halo, subscriber);
        }
    }

    @Override
    public void setAuthProfile(@NonNull HaloAuthProfile haloAuthProfile) {
        return;
    }

    @Override
    public void setSocialToken(@NonNull String socialToken) {
        mFacebookToken = socialToken;
    }


    @Override
    public void release() {
        if (mFacebookSubscription != null) {
            mFacebookSubscription.unsubscribe();
            mFacebookSubscription = null;
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
            success(resultData);
        } else if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_CANCELED_CODE) { //Cancelled
            haloSocialProfileHaloResult = new HaloResultV2<>(HaloStatus.builder().cancel().build(), null);
        } else if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_ERROR_CODE) { // Error
            haloSocialProfileHaloResult = error(resultData);
        }
        emitResult();
    }

    /**
     * Launch Facebook activity to request the facebook token
     *
     * @param halo
     * @param subscriber
     */
    private void launchFacebookActivity(@NonNull Halo halo, @NonNull Subscriber subscriber) {
        mFacebookSubscription = halo.framework().subscribe(subscriber, EventId.create(HaloFacebookSignInActivity.Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED));
        HaloFacebookSignInActivity.startActivity(halo.context());
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

    /**
     * Login with halo and submit result to
     */
    private void emitResult() {
        if (haloSocialProfileHaloResult == null) {
            loginWithANetwork().execute(new CallbackV2<IdentifiedUser>() {
                @Override
                public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                    if (result.status().isOk()) {
                        //Finish the callback
                        userRequestCallbak.onFinish(result);
                        //Release the login resources
                        release();
                    }
                }
            });
        } else if (userRequestCallbak != null) {
            //Finish the callback
            userRequestCallbak.onFinish(haloSocialProfileHaloResult);
            //Release the login resources
            release();
        }
    }

    /**
     * Tries to login with halo based on the social network token, network type and device alias
     */
    @NonNull
    private HaloInteractorExecutor<IdentifiedUser> loginWithANetwork() {
        HaloSocialApi socialApi = (HaloSocialApi) Halo.instance().manager().haloSocial();
        return new HaloInteractorExecutor<>(Halo.instance(),
                "Login with a social provider",
                new SocialLoginInteractor(socialApi.accountType(), new LoginRepository(new LoginRemoteDatasource(Halo.instance().framework().network())),
                        getSocialNetworkName(), mFacebookToken, Halo.instance().manager().getDevice().getAlias(), socialApi.recoveryPolicy())
        );

    }

    /**
     * Generates the result for success.
     *
     * @param bundle The bundle with the result.
     * @return The result created.
     */
    private void success(@NonNull Bundle bundle) {
        mFacebookToken = bundle.getString(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ACCOUNT);
    }

    /**
     * Generates the error result.
     *
     * @param bundle The bundle containing the data.
     * @return The result.
     */
    @NonNull
    private HaloResultV2<IdentifiedUser> error(@NonNull Bundle bundle) {
        HaloIntegrationException e = (HaloIntegrationException) bundle.getSerializable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ERROR);
        HaloStatus status = HaloStatus.builder()
                .error(new HaloIntegrationException("Error integrating the facebook login", e))
                .build();
        return new HaloResultV2<>(status, null);
    }
}