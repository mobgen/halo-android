package com.mobgen.halo.android.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * Special type of analytic wrapper to log request information.
 */
public class RequestAnalytic {

    /**
     * The analytic instance that is wrapped by the request analytic.
     */
    private Analytic mAnalytic;

    /**
     * Creates the analytic based on the params passed to the request builder.
     *
     * @param analytic The analytic constructed.
     */
    private RequestAnalytic(@NonNull Analytic analytic) {
        AssertionUtils.notNull(analytic, "analytic");
        mAnalytic = analytic;
    }

    /**
     * Provides the analytic wrapped.
     *
     * @return The analytic wrapped.
     */
    @Api(2.0)
    @NonNull
    public Analytic getAnalytic() {
        return mAnalytic;
    }

    /**
     * Creates a new builder for the request analytic.
     *
     * @param name The name for the analytic itself.
     * @return The builder.
     */
    @Api(2.0)
    @NonNull
    public static RequestAnalytic.Builder create(@Size(min = 1, max = 32) @NonNull String name) {
        return new Builder(name);
    }

    /**
     * Builder class to construct analytics for requests.
     */
    public static class Builder implements IBuilder<RequestAnalytic> {

        /**
         * The time the request has been loaded.
         */
        @Nullable
        private Long mRequestTime;
        /**
         * The request method.
         */
        private String mRequestMethod;
        /**
         * The url of the request.
         */
        private String mUrl;
        /**
         * The name of the analytic.
         */
        private final String mName;

        /**
         * The builder constructor. Use {@link RequestAnalytic#create(String)} to create it.
         *
         * @param name The name of the analytic.
         */
        private Builder(@Size(min = 1, max = 32) @NonNull String name) {
            mName = name;
        }

        /**
         * The time the request takes to be executed.
         *
         * @param millis The number of milliseconds that the request took.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder time(@Nullable Long millis) {
            mRequestTime = millis;
            return this;
        }

        /**
         * The url of the request. This field is mandatory.
         *
         * @param url The url.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder url(@NonNull String url) {
            AssertionUtils.notNull(url, "url");
            mUrl = url;
            return this;
        }

        /**
         * Adds the request method. This field is mandatory.
         *
         * @param method The request method for this request.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder requestMethod(@NonNull String method) {
            mRequestMethod = method;
            return this;
        }

        @Api(2.0)
        @NonNull
        @Override
        public RequestAnalytic build() {
            AssertionUtils.notNull(mUrl, "url");
            AssertionUtils.notNull(mRequestMethod, "requestMethod");
            AssertionUtils.notNull(mName, "name");
            Analytic analytic = new Analytic(mName)
                    .addParam(Analytic.Param.URL, mUrl)
                    .addParam(Analytic.Param.REQUEST_METHOD, mRequestMethod);
            if (mRequestTime != null) {
                analytic.addParam(Analytic.Param.TIME, mRequestTime);
            }
            return new RequestAnalytic(analytic);
        }
    }
}
