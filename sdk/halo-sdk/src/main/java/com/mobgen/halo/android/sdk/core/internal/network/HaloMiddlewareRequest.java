package com.mobgen.halo.android.sdk.core.internal.network;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Allows to access custom middleware elements in HALO.
 */
@Keep
public class HaloMiddlewareRequest {
    /**
     * The request to execute.
     */
    @NonNull
    private HaloRequest mRequest;
    /**
     * The builder used to create this request.
     */
    @NonNull
    private Builder mBuilder;

    /**
     * THe custom request generation.
     *
     * @param request The request to execute.
     * @param builder The callback.
     */
    HaloMiddlewareRequest(@NonNull HaloRequest request, @NonNull Builder builder) {
        mRequest = request;
        mBuilder = builder;
    }

    /**
     * Factory method for the builder.
     *
     * @param api The client that will be used to perform the request.
     * @return The builder created.
     */
    @Api(1.1)
    public static Builder builder(@NonNull HaloNetworkApi api) {
        AssertionUtils.notNull(api, "api");
        return new Builder(api);
    }

    /**
     * Executes the custom request.
     *
     * @return The response after an execution.
     * @throws HaloNetException Exception executing on the network.
     */
    @Api(1.1)
    public Response execute() throws HaloNetException {
        return mBuilder.mClient.request(mRequest);
    }

    /**
     * Provides the url of this request.
     *
     * @return The url.
     */
    @Api(1.2)
    @NonNull
    public String url() {
        return mRequest.buildOkRequest().url().toString();
    }

    /**
     * Builder for the custom request.
     */
    @Keep
    public static class Builder implements IBuilder<HaloMiddlewareRequest> {

        /**
         * The company name.
         */
        private String mCompany;

        /**
         * The middleware type name.
         */
        private String mModuleType;

        /**
         * Determines if it has a proxy or not.
         */
        private boolean mHasProxy;

        /**
         * The url of the request like cake/{instance}
         */
        private String mUrl;

        /**
         * The params for the url.
         */
        private Map<String, String> mParams;

        /**
         * The multi-params for the url.
         */
        private Map<String, String[]> mMultiParams;

        /**
         * The wrapper request builder.
         */
        private HaloRequest.Builder mRequestBuilder;

        /**
         * The client.
         */
        private HaloNetClient mClient;

        /**
         * The builder for the custom request.
         */
        Builder(HaloNetworkApi api) {
            mRequestBuilder = HaloRequest.builder(api);
            mClient = api.client();
        }

        /**
         * The custom middleware information.
         *
         * @param companyName The company name information.
         * @param type        The middleware type name.
         * @return The current builder.
         */
        @Api(1.2)
        public Builder middleware(@NonNull String companyName, @NonNull String type) {
            mCompany = companyName;
            mModuleType = type;
            return this;
        }

        /**
         * Adds the url to the builder.
         *
         * @param url The ur.
         * @return The current builder.
         */
        @Api(1.1)
        public Builder url(String url) {
            url(url, null, null);
            return this;
        }

        /**
         * Adds the proxy configuration to the custom url.
         * A proxied url looks like: '/api/---proxy---/:moduleTypePath/:rest*'
         *
         * @param hasProxy True if it has a proxy, false otherwise.
         * @return The current builder.
         */
        @Api(1.2)
        @NonNull
        public Builder hasProxy(boolean hasProxy) {
            mHasProxy = hasProxy;
            return this;
        }

        /**
         * Adds the url to the builder and parses the given params.
         *
         * @param url    The url to add.
         * @param params The params that will be parsed.
         * @return The current builder.
         */
        @Api(1.1)
        public Builder url(String url, Map<String, String> params) {
            url(url, params, null);
            return this;
        }

        /**
         * Adds the url to the builder and parses the params and multiparams if they are available.
         *
         * @param url         The url to add.
         * @param params      The params to parse.
         * @param multiparams The multiparams support.
         * @return The current builder.
         */
        @Api(1.1)
        public Builder url(String url, Map<String, String> params, Map<String, String[]> multiparams) {
            mUrl = url;
            mParams = params;
            mMultiParams = multiparams;
            return this;
        }

        /**
         * Sets a tag that can be used lately to cancel a request.
         *
         * @param tag The tag that will be used to cancel a request.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder tag(Object tag) {
            mRequestBuilder.tag(tag);
            return this;
        }

        /**
         * The body for the request.
         *
         * @param body The body built based on the HaloBodyFactory.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder body(@Nullable RequestBody body) {
            mRequestBuilder.body(body);
            return this;
        }

        /**
         * Adds a header on the request.
         *
         * @param key   The key name of the header.
         * @param value The value of the header.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder header(@NonNull String key, @NonNull String value) {
            mRequestBuilder.header(key, value);
            return this;
        }

        /**
         * Puts the method of the request. If not provided the method will be inferred from the
         * parameters provided.
         *
         * @param method The method of the request.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder method(@Nullable HaloRequestMethod method) {
            mRequestBuilder.method(method);
            return this;
        }

        @Api(1.1)
        @NonNull
        @Override
        public HaloMiddlewareRequest build() {
            if (mModuleType == null || mCompany == null || mUrl == null) {
                throw new IllegalArgumentException("You should provide the module type and the company name. Use module() method.");
            }
            String proxy = mHasProxy ? "---proxy---/" : "";
            String fullCall = "/api/" + proxy + mCompany + "-" + mModuleType + "/" + mUrl;
            mRequestBuilder.url(HaloNetworkConstants.HALO_ENDPOINT_ID, fullCall, mParams, mMultiParams);
            return new HaloMiddlewareRequest(mRequestBuilder.build(), this);
        }
    }
}
