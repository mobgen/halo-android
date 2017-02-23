package com.mobgen.halo.android.twofactor;

import android.content.IntentFilter;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

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

/**
 * The two factor authentication API is a wrapper library that allows you
 * to receive and manage notifications (from sms or push provider) inside the current
 * application. You will get a two factor authentication code.
 *
 * To use the notifications api you need a valid instance of the HALO SDK with valid credentials.
 */
@Keep
public class HaloTwoFactorApi extends HaloPluginApi {
    /**
     * The sms provider name.
     */
    private String smsProvider = "HALO";

    /**
     * The two factor push issuer.
     */
    public static final String TWO_FACTOR_NOTIFICATION_ISSUER = "issuer_push";

    /**
     * The two factor sms issuer.
     */
    public static final String TWO_FACTOR_SMS_ISSUER = "issuer_sms";
    /**
     * The two factor push notifiaction subscription.
     */
    private ISubscription twoFactorNotification;

    /**
     * The two factor sms notification subscription.
     */
    private ISubscription twoFactorSMS;

    /**
     * The sms provider state.
     */
    private Boolean smsNotification = false;

    /**-
     * The push provider state.
     */
    private Boolean pushNotification = false;


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
     * @return The two factor authentication api instance.
     */
    @Keep
    public static Builder with(@NonNull Halo halo) {
        return new Builder(halo);
    }

    /**
     * The listener to receive the notifications from push or sms providers.
     *
     * @param haloTwoFactorAttemptListener The two factor attempt listener.
     */
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

    /**
     * Release in a safe way all the listeners when you are ready.
     */
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

    /**
     * Get the sms provider name to listen for.
     *
     * @return The sms provider name to use. By default it returns HALO
     */
    @NonNull
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
     * The builder for the two factor authentication api.
     */
    @Keep
    public static class Builder implements IBuilder<HaloTwoFactorApi> {

        /**
         * The two factor authentication api.
         */
        @NonNull
        private HaloTwoFactorApi mTwoFactorApi;

        /**
         * The social api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull final Halo halo) {
            mTwoFactorApi = new HaloTwoFactorApi(halo);
        }

        /**
         * Set the name of the sms provider.
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
         * Adds the push provider to the current two factor authentication instance.
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
         * Adds the sms provider to the current two factor authentication instance.
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