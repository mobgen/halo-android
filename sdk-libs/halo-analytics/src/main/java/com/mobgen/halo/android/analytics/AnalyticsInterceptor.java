package com.mobgen.halo.android.analytics;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to log the request information that will be attached to HALO core.
 */
public class AnalyticsInterceptor implements Interceptor {

    /**
     * The analytics instance to log the relevant information.
     */
    private HaloAnalyticsApi mAnalytics;

    /**
     * Constructor for the interceptor.
     *
     * @param haloAnalytics The analytics.
     */
    protected AnalyticsInterceptor(@NonNull HaloAnalyticsApi haloAnalytics) {
        mAnalytics = haloAnalytics;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        long initialTime = System.currentTimeMillis();
        Request request = chain.request();
        Response response = chain.proceed(request);
        long elapsedTime = System.currentTimeMillis() - initialTime;
        mAnalytics.logRequestAnalytic(RequestAnalytic.create(Analytic.Type.REQUEST)
                .requestMethod(request.method())
                .time(elapsedTime)
                .url(request.url().toString())
                .build());
        return response;
    }

    /**
     * Gives access to the analytics provider wrapped.
     * @return The provider.
     */
    @Api(2.0)
    @NonNull
    public AnalyticsProvider provider() {
        return mAnalytics.provider();
    }
}
