package com.mobgen.halo.android.framework.network.interceptors;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Halo interceptor class that abstracts from chaining the interceptors.
 * It is a simple wrap over OkHttp.
 */
public abstract class RequestResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = interceptRequest(request, chain.connection());
        Response response = chain.proceed(request);
        response = interceptResponse(request, response);
        return response;
    }

    /**
     * Intercepts the requests based on the needs.
     *
     * @param request    The request to process.
     * @param connection The connection object.
     * @return The request processed.
     */
    @Api(1.0)
    public Request interceptRequest(Request request, Connection connection) {
        return request;
    }

    /**
     * Intercepts the response based on the needs.
     *
     * @param request  The request intercepted.
     * @param response The response intercepted.
     * @return The response processed.
     */
    @Api(1.0)
    public Response interceptResponse(Request request, Response response) {
        return response;
    }
}
