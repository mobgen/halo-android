package com.mobgen.halo.android.notifications.services;

import android.annotation.SuppressLint;
import android.support.annotation.Keep;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

/**
 * @hide Allows the token retrieval from the instance ID Google Api. The token retrieved here, when refreshed should be
 * send to the Halo server with its tags.
 */
@Keep
@SuppressLint("Registered")
public class InstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Halog.d(getClass(), "Token refresh process started.");
        NotificationEmitter.emitRefreshToken(getApplicationContext());
    }

    /**
     * The refresh token listener for notifications.
     */
    public interface RefreshNotificationTokenListener {

        /**
         * Calls to refresh the token.
         */
        void onRefreshToken();
    }
}
