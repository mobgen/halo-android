package com.mobgen.halo.android.social.facebook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.FacebookSdk;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
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
 * The social provider for facebook authentication.
 */
public class FacebookSocialProvider implements SocialProvider, Subscriber {

    /**
     * Pending callback for the facebook login.
     */
    private Callback<HaloSocialProfile, Void> mPendingCallbackResolution;
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
    public boolean linkedAppAvailable(@NonNull Context context) {
        return true;
    }

    @Override
    public void authenticate(@NonNull Halo halo, @NonNull Callback<HaloSocialProfile, Void> callback) {
        initIfNeeded(halo.context());
        if (mPendingCallbackResolution == null) {
            mPendingCallbackResolution = callback;
            mFacebookSubscription = halo.framework().subscribe(this, EventId.create(HaloFacebookSignInActivity.Result.EVENT_NAME_FACEBOOK_SIGN_IN_FINISHED));
            HaloFacebookSignInActivity.startActivity(halo.context());
        }
    }

    @Override
    public void release() {
        if (mFacebookSubscription != null) {
            mFacebookSubscription.unsubscribe();
            mFacebookSubscription = null;
        }
        mPendingCallbackResolution = null;
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
        HaloResult<HaloSocialProfile, Void> result = null;
        if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_SUCCESS_CODE) { //Success
            result = success(resultData);
        } else if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_CANCELED_CODE) { //Cancelled
            result = new HaloResult<>(HaloStatus.builder().cancel().build(), null);
        } else if (loginEventResult == HaloFacebookSignInActivity.Result.FACEBOOK_ERROR_CODE) { // Error
            result = error(resultData);
        }

        //Result must not be null, some error in configuration
        if (result == null) {
            String msg = "This should never happen. An event must have a FACEBOOK_SIGN_IN_RESULT. Contact the halo support.";
            Halog.wtf(getClass(), msg);
            throw new IllegalStateException(msg);
        }

        //Finish the callback
        mPendingCallbackResolution.onFinish(result);

        //Release the login resources
        release();
    }

    /**
     * Generates the result for success.
     *
     * @param bundle The bundle with the result.
     * @return The result created.
     */
    @NonNull
    private HaloResult<HaloSocialProfile, Void> success(@NonNull Bundle bundle) {
        HaloSocialProfile profile = bundle.getParcelable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ACCOUNT);
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
        HaloIntegrationException e = (HaloIntegrationException) bundle.getSerializable(HaloFacebookSignInActivity.Result.FACEBOOK_SIGN_IN_ERROR);
        HaloStatus status = HaloStatus.builder()
                .error(new HaloIntegrationException("Error integrating the facebook login", e))
                .build();
        return new HaloResult<>(status, null);
    }
}