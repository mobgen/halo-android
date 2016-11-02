package com.mobgen.halo.android.analytics;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.net.client.HaloNetClient;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Analytics plugin for Halo.
 */
public class HaloAnalyticsApi extends HaloPluginApi {

    /**
     * The analytics provider.
     */
    private AnalyticsProvider mProvider;

    /**
     * Constructor for the analytics.
     * @param builder The current builder.
     */
    private HaloAnalyticsApi(@NonNull Builder builder) {
        super(builder.mHalo);
        mProvider = builder.mProvider;
        if(builder.mLogNetworkRequests){
            attachNetworkLogging();
        }
    }

    /**
     * Analyzes the network by attaching an interceptor that sends the request analytics logging some
     * information from them. The parameter logNetwork from the builder attaches it by default.
     */
    @Api(2.0)
    public void attachNetworkLogging() {
        HaloNetClient client = framework().network().client();
        OkHttpClient.Builder overloadedClient = client.ok().newBuilder();
        removePreviousInterceptor(overloadedClient);
        overloadedClient.addInterceptor(new AnalyticsInterceptor(this));
        client.overrideOk(overloadedClient);
    }

    /**
     * Detaches the network logging of the current analytics.
     */
    @Api(2.0)
    public void detachNetworkLogging(){
        HaloNetClient client = framework().network().client();
        OkHttpClient.Builder overloadedClient = client.ok().newBuilder();
        removePreviousInterceptor(overloadedClient);
        client.overrideOk(overloadedClient);
    }

    /**
     * Logs the analytic into the analytics provider.
     * @param analytic The analytic to be logged.
     * @return The analytics provider so you can chain many analytics in the same line.
     */
    @Api(2.0)
    @NonNull
    public HaloAnalyticsApi logAnalytic(@NonNull Analytic analytic){
        AssertionUtils.notNull(analytic, "analytic");
        mProvider.logAnalytic(analytic);
        return this;
    }

    /**
     * Logs a request analytic.
     * @param requestAnalytic The request analytic to be logged.
     * @return The analytics instance.
     */
    @Api(2.0)
    @NonNull
    public HaloAnalyticsApi logRequestAnalytic(@NonNull RequestAnalytic requestAnalytic){
        AssertionUtils.notNull(requestAnalytic, "requestAnalytic");
        mProvider.logAnalytic(requestAnalytic.getAnalytic());
        return this;
    }

    /**
     * Logs a transaction analytic into the analytics.
     * @param transactionAnalytic The transaction analytic.
     * @return The analytics.
     */
    @Api(2.0)
    @NonNull
    public HaloAnalyticsApi logTransactionAnalytic(@NonNull TransactionAnalytic transactionAnalytic){
        AssertionUtils.notNull(transactionAnalytic, "transactionAnalytic");
        mProvider.logAnalytic(transactionAnalytic.getAnalytic());
        return this;
    }

    /**
     * Gives access to the internal provider.
     * @return The provider.
     */
    @Api(2.0)
    @NonNull
    public AnalyticsProvider provider(){
        return mProvider;
    }

    /**
     * Factory to create the analytics.
     * @param halo The halo instance.
     * @return The analytics plugin builder..
     */
    @Api(2.0)
    public static Builder with(@NonNull Halo halo){
        AssertionUtils.notNull(halo, "halo");
        return new Builder(halo);
    }

    /**
     * Removes the current interceptor from the previous client.
     * @param newClient The client to process.
     */
    private void removePreviousInterceptor(@NonNull OkHttpClient.Builder newClient){
        //Remove previous interceptors of the same type
        for (int i = 0; i < newClient.interceptors().size(); i++) {
            Interceptor interceptor = newClient.interceptors().get(i);
            if(interceptor instanceof AnalyticsInterceptor && ((AnalyticsInterceptor) interceptor).provider().getClass() == mProvider.getClass()){
                newClient.interceptors().remove(i);
            }
        }
    }

    public static class Builder implements IBuilder<HaloAnalyticsApi> {

        /**
         * The halo instance.
         */
        private Halo mHalo;

        /**
         * Parameter that specifies if the network requests must be logged as part of the analytics.
         */
        private boolean mLogNetworkRequests;

        /**
         * The analytics provider.
         */
        private AnalyticsProvider mProvider;

        /**
         * The builder for the analytics.
         * @param halo The halo instance.
         */
        @Api(2.0)
        private Builder(@NonNull Halo halo){
            mHalo = halo;
        }

        /**
         * The provider for the analytics.
         * @param provider The provider.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder provider(@NonNull AnalyticsProvider provider){
            mProvider = provider;
            return this;
        }

        /**
         * Attaches an interceptor in the networking module to log all the
         * operations performed using HALO.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder logNetwork(){
            mLogNetworkRequests = true;
            return this;
        }

        @NonNull
        @Override
        public HaloAnalyticsApi build() {
            AssertionUtils.notNull(mProvider, "provider");
            return new HaloAnalyticsApi(this);
        }
    }
}
