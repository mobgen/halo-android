package com.mobgen.halo.android.framework.network.client.request;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.BuildConfig;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.sessions.HaloSession;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Version;

/**
 * Request wrapper to be executed.
 */
public class HaloRequest {

    /**
     * Cache header.
     */
    public static final String SERVER_CACHE_HEADER = "to-cache";

    /**
     * Cache control.
     */
    public static final String CACHE_CONTROL_HEADER = "cache-control";

    /**
     * No cache.
     */
    public static final String NO_CACHE = "no-cache";

    /**
     * User Agent header
     */
    public static final String USER_AGENT_HEADER = "User-Agent";

    /**
     * Request created.
     */
    private final Request.Builder mRequestBuilder;

    /**
     * The response parser.
     */
    private final Parser.Factory mResponseParser;

    /**
     * The networking api.
     */
    private final HaloNetClient mNetworkClient;

    /**
     * Constructor for the halo request.
     *
     * @param builder The builder of the request.
     */
    private HaloRequest(@NonNull Builder builder) {
        mNetworkClient = builder.mNetworkApi.client();
        mRequestBuilder = builder.mRequestBuilder;
        mResponseParser = builder.mResponseParser;
    }

    /**
     * Creates a new request builder.
     *
     * @param networkApi The networking api.
     * @return The request builder.
     */
    @Api(1.3)
    @NonNull
    public static Builder builder(@NonNull HaloNetworkApi networkApi) {
        AssertionUtils.notNull(networkApi, "networkApi");
        return new Builder(networkApi);
    }

    /**
     * Executes this request and provides a response as result.
     *
     * @return The response generated.
     * @throws HaloNetException Error while executing the request.
     */
    @Api(1.3)
    @NonNull
    public Response execute() throws HaloNetException {
        return mNetworkClient.request(this);
    }

    /**
     * Executes this request and provides a response as result.
     *
     * @param clazz The class to use for the execution and parsing.
     * @return The response generated.
     * @throws HaloNetException Error while executing the request.
     */
    @Api(1.3)
    @NonNull
    public <T> T execute(Class<T> clazz) throws HaloNetException {
        return mNetworkClient.request(this, clazz);
    }

    /**
     * Executes this request and provides a response as result.
     *
     * @param type The class to which it will be parsed as list.
     * @return The response generated.
     * @throws HaloNetException Error while executing the request.
     */
    @Api(2.0)
    @NonNull
    @SuppressWarnings("unchecked")
    public <T> T execute(@NonNull TypeReference<T> type) throws HaloNetException {
        return mNetworkClient.request(this, type);
    }

    /**
     * Provides the OkHttp request. We discourage to use this method since it is a framework one and
     * the instance you produce here will noe be reused.
     *
     * @return The internal request.
     */
    @Api(2.0)
    @NonNull
    public Request buildOkRequest() {
        return mRequestBuilder.build();
    }

    /**
     * Provides the response parser for this request.
     *
     * @return The response parser.
     */
    @Api(2.0)
    @NonNull
    public Parser.Factory getParser() {
        return mResponseParser;
    }

    /**
     * Installer for the request.
     */
    public static class Builder implements IBuilder<HaloRequest> {

        /**
         * The networking api.
         */
        private final HaloNetworkApi mNetworkApi;

        /**
         * OkHttp request builder.
         */
        private final Request.Builder mRequestBuilder;

        /**
         * The current okhttp request builder.
         */
        private RequestBody mRequestBody;

        /**
         * The request method used.
         */
        private HaloRequestMethod mHttpMethod;

        /**
         * w
         * Sets a response parser on the request.
         */
        private Parser.Factory mResponseParser;

        /**
         * The session to be used with this request.
         */
        private HaloSession mSession;

        /**
         * Constructor for the request builder.
         *
         * @param networkApi The networking api.
         */
        private Builder(@NonNull HaloNetworkApi networkApi) {
            mNetworkApi = networkApi;
            mResponseParser = networkApi.framework().parser();
            mRequestBuilder = new Request.Builder();
        }

        /**
         * Sets the url of the request.
         *
         * @param endpointId The endpoint id of the request.
         * @param call       The url of the specific request.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder url(@NonNull String endpointId, @NonNull String call) {
            String fullUrl = mNetworkApi.requestUrl(endpointId, call);
            mRequestBuilder.url(fullUrl);
            return this;
        }

        /**
         * Allows to generate urls that has parameters that can be replaced.
         *
         * @param endpointId The endpoint id of the request.
         * @param call       The url of the specific request. This can include params between {param} to apply substitutions.
         * @param params     The parameters in a map string format.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder url(@NonNull String endpointId, @NonNull String call, @Nullable Map<String, String> params) {
            url(endpointId, parseUrlParams(call, params));
            return this;
        }

        /**
         * Array params parsing allowed in this url call.
         *
         * @param endpointId  The endpoint id of the request.
         * @param call        The url to parse for array params.
         * @param params      The params for simple parsing. Use the notation paramName={paramParsingKey}. This also allows url rest parameters.
         * @param multiParams The array parameter parsing. To add this parameters use the notation {paramArrayName[]}
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder url(@NonNull String endpointId, @NonNull String call, @Nullable Map<String, String> params, @Nullable Map<String, String[]> multiParams) {
            String newUrl = parseUrlParams(call, params);
            //Parse multiparams
            if (multiParams != null) {
                for (Map.Entry<String, String[]> entry : multiParams.entrySet()) {
                    StringBuilder builder = new StringBuilder();
                    boolean hasAmpersand = false;
                    for (String element : entry.getValue()) {
                        if (hasAmpersand) {
                            builder.append("&");
                        }
                        builder.append(entry.getKey()).append("[]=").append(element);
                        hasAmpersand = true;
                    }
                    newUrl = newUrl.replace("{" + entry.getKey() + "[]}", builder.toString());
                }
            }
            url(endpointId, newUrl);
            return this;
        }

        /**
         * Provides the url parsing the given params.
         *
         * @param call   The url.
         * @param params The params to parse.
         * @return The returned value.
         */
        private String parseUrlParams(@NonNull String call, @Nullable Map<String, String> params) {
            String newUrl = call;
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    newUrl = newUrl.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            }
            return newUrl;
        }

        /**
         * Sets a tag that can be used lately to cancel a request.
         *
         * @param tag The tag that will be used to cancel a request.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder tag(@Nullable Object tag) {
            mRequestBuilder.tag(tag);
            return this;
        }

        /**
         * The body for the request.
         *
         * @param body The body built based on the HaloBodyFactory.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder body(@Nullable RequestBody body) {
            mRequestBody = body;
            return this;
        }

        /**
         * Adds a header on the request.
         *
         * @param key   The key name of the header.
         * @param value The value of the header.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder header(@NonNull String key, @NonNull String value) {
            AssertionUtils.notNull(key, "key");
            AssertionUtils.notNull(value, "value");
            mRequestBuilder.addHeader(key, value);
            return this;
        }

        /**
         * Adds a cache header to the request.
         *
         * @param timeInSeconds The value of the cache header in seconds.
         * @return The current builder.
         */
        @Api(2.33)
        @NonNull
        public Builder cacheHeader(int timeInSeconds) {
            mRequestBuilder.addHeader(SERVER_CACHE_HEADER, String.valueOf(timeInSeconds));
            return this;
        }

        /**
         * Adds a cache control header to the request.
         *
         * @param cacheControl The cache control policy to apply.
         * @return The current builder
         */
        @Api(2.4)
        @NonNull
        public Builder cacheControl(String cacheControl) {
            mRequestBuilder.addHeader(CACHE_CONTROL_HEADER, cacheControl);
            return this;
        }

        /**
         * Sets the session on this request.
         *
         * @param session The session for this request.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder session(@Nullable HaloSession session) {
            mSession = session;
            return this;
        }

        /**
         * Puts the method of the request. If not provided the method will be inferred from the
         * parameters provided.
         *
         * @param method The method of the request.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder method(@Nullable HaloRequestMethod method) {
            AssertionUtils.notNull(method, "method");
            mHttpMethod = method;
            return this;
        }

        /**
         * Sets the response parser for this request in case it is needed.
         *
         * @param parser The parser for the request.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder responseParser(@NonNull Parser.Factory parser) {
            AssertionUtils.notNull(parser, "parser");
            mResponseParser = parser;
            return this;
        }

        /**
         * Builds the request.
         *
         * @return A new halo request.
         */
        @Api(2.0)
        @NonNull
        @Override
        public HaloRequest build() {
            if (mHttpMethod != null) {
                mRequestBuilder.method(mHttpMethod.toString(), mRequestBody);
            } else if (mRequestBody != null) { //Default it is a post request
                mRequestBuilder.post(mRequestBody);
            }

            if (mSession != null) {
                mRequestBuilder.addHeader("Authorization", mSession.getSessionAuthentication());
            }

            mRequestBuilder.addHeader(USER_AGENT_HEADER, getUserAgent());

            return new HaloRequest(this);
        }

        /**
         * Provides a userAgent in order to use it like an identifier header of every requests.
         *
         * @return The User-Agent header content.
         */
        private String getUserAgent() {
            ApplicationInfo applicationInfo = mNetworkApi.context().getApplicationInfo();
            String packageName = applicationInfo.packageName;
            StringBuilder userAgent = new StringBuilder("");

            if (applicationInfo.labelRes != 0) {
                String applicationName = mNetworkApi.context().getString(applicationInfo.labelRes);
                userAgent.append(applicationName.replace(" ", ""));

                try {
                    PackageInfo packageInfo = mNetworkApi.context().getPackageManager().getPackageInfo(packageName, 0);
                    userAgent.append("/");
                    userAgent.append(packageInfo.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    Halog.e(getClass(), "The application version could not be collected for this execution.");
                }

                userAgent.append(" ");
            } else {
                userAgent.append("UnknownApplication ");
            }

            userAgent.append("Android/");
            userAgent.append(Build.VERSION.RELEASE);
            userAgent.append(" ");
            userAgent.append("OkHttp/");
            userAgent.append(Version.userAgent());
            userAgent.append(" ");
            userAgent.append("HaloAndroidSDK/");
            userAgent.append(BuildConfig.VERSION_NAME);

            return userAgent.toString();
        }
    }
}
