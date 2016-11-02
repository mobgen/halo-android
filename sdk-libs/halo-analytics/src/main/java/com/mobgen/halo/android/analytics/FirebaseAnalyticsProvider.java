package com.mobgen.halo.android.analytics;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The firebase analytics provider to execute the halo analytics.
 */
public class FirebaseAnalyticsProvider implements AnalyticsProvider {

    /**
     * The analytics instance for firebase.
     */
    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * Constructor for the provider for firebase.
     * @param context The context.
     */
    @Api(2.0)
    public FirebaseAnalyticsProvider(@NonNull Context context){
        //Unbox the context just in case
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
    }

    @Api(2.0)
    @Override
    public void logAnalytic(@NonNull Analytic analytic) {
        mFirebaseAnalytics.logEvent(analytic.name(), analytic.params());
    }
}
