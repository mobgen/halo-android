package com.mobgen.halo.android.framework.network.interceptors;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor that logs the profiling information of the requests. It prints relevant information for the request
 * and the time elapsed.
 */
public class HaloProfilerInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Long requestTime = System.nanoTime();
        Response response = chain.proceed(request);
        long elapsedTime = System.nanoTime() - requestTime;
        Halog.d(getClass(), "Request completed in " + elapsedTime / 1000000 + "ms.");
        return response;
    }
}
