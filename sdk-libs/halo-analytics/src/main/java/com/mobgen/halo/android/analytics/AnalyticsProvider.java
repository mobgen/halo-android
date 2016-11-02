package com.mobgen.halo.android.analytics;

import android.support.annotation.NonNull;

/**
 * The analytics provider is a common interface to log analytics.
 */
public interface AnalyticsProvider {

    /**
     * Logs the analytic into the analytics provider.
     * @param analytic The analytic to be logged.
     */
    void logAnalytic(@NonNull Analytic analytic);
}
