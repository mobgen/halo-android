package com.mobgen.halo.android.twofactor;

import android.Manifest;
import android.content.IntentFilter;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;


import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.twofactor.callbacks.HaloTwoFactorAttemptListener;
import com.mobgen.halo.android.twofactor.callbacks.HaloTwoFactorNotification;
import com.mobgen.halo.android.twofactor.callbacks.HaloTwoFactorSMS;
import com.mobgen.halo.android.twofactor.receiver.HaloSMSSubscription;



@Keep
public class HaloTwoFactorApi extends HaloPluginApi {

    private ISubscription twoFactorNotification;

    private ISubscription twoFactorSMS;

    private Boolean smsNotification = false;

    private Boolean pushNotification = false;

    private String smsProvider = "HALO";

    public static final String TWO_FACTOR_NOTIFICATION_ISSUER = "issuer_push";

    public static final String TWO_FACTOR_SMS_ISSUER = "issuer_sms";

    private HaloNotificationsApi mHaloNotificationApi;

    /**
     * Intent filter to refresh the token.
     */
    private static final String NOTIFICATION_TWO_FACTOR = "android.provider.Telephony.SMS_RECEIVED";


    /**
     * Constructor that accepts halo.
     *
     * @param halo The halo instance.
     */
    private HaloTwoFactorApi(@NonNull Halo halo) {
        super(halo);
    }

    /**
     * Creates the two factor api for authentications.
     *
     * @param halo The halo instance.
     * @return The social api instance.
     */
    @Keep
    public static Builder with(@NonNull Halo halo) {
        return new Builder(halo);
    }


    @Keep
    @Api(2.3)
    public void listenTwoFactorAttempt(@NonNull HaloTwoFactorAttemptListener haloTwoFactorAttemptListener) {
        if(pushNotification) {
            mHaloNotificationApi = HaloNotificationsApi.with(halo());
            twoFactorNotification = mHaloNotificationApi.listenTwoFactorNotifications(new HaloTwoFactorNotification(haloTwoFactorAttemptListener));
        }
        if(smsNotification){
            twoFactorSMS = new HaloSMSSubscription(halo().context(),new HaloTwoFactorSMS(haloTwoFactorAttemptListener),getFilterFor(NOTIFICATION_TWO_FACTOR),getSMSProviderName());
        }
    }

    @Keep
    @Api(2.3)
    public void release(){
        if(twoFactorNotification!=null) {
            mHaloNotificationApi.release();
            twoFactorNotification.unsubscribe();
        }
        if(twoFactorSMS!=null) {
            twoFactorSMS.unsubscribe();
        }
    }

    private String getSMSProviderName(){
        return smsProvider;
    }

    /**
     * Provides the filter for the event id provided.
     *
     * @param eventId The event id.
     * @return The intent filter.
     */
    @NonNull
    private static IntentFilter getFilterFor(@NonNull String eventId) {
        IntentFilter filterAction = new IntentFilter(eventId);
        filterAction.setPriority(999);
        return filterAction;
    }


    /**
     * The builder for the social api.
     */
    @Keep
    public static class Builder implements IBuilder<HaloTwoFactorApi> {

        /**
         * The social api.
         */
        @NonNull
        private HaloTwoFactorApi mTwoFactorApi;

        private Halo mHalo;


        /**
         * The social api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull final Halo halo) {
            mHalo = halo;
            mTwoFactorApi = new HaloTwoFactorApi(halo);
        }

        /**
         * Adds the halo provider to the social api login.
         *
         * @return The current builder.
         */
        @Keep
        @Api(2.3)
        @NonNull
        public Builder smsProvider(String name) {
            mTwoFactorApi.smsProvider = name;
            return this;
        }

        /**
         * Adds the halo provider to the social api login.
         *
         * @return The current builder.
         */
        @Keep
        @Api(2.3)
        @NonNull
        public Builder withNotifications() {
            mTwoFactorApi.pushNotification = true;
            return this;
        }

        /**
         * Adds the facebook provider to the social api login.
         *
         * @return The current builder.
         */
        @Keep
        @Api(2.3)
        @NonNull
        public Builder withSMS() {
            mTwoFactorApi.smsNotification = true;
            return this;
        }


        @NonNull
        @Override
        public HaloTwoFactorApi build() {
            return mTwoFactorApi;
        }
    }
}