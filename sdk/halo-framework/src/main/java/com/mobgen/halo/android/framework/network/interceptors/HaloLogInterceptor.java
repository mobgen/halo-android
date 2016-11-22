package com.mobgen.halo.android.framework.network.interceptors;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Logs the requests and the responses produced.
 */
public class HaloLogInterceptor extends RequestResponseInterceptor {

    /**
     * Intercepts the request and logs the url, headers and method type.
     *
     * @param request    The request to process.
     * @param connection The connection object.
     * @return The same request.
     */
    @Override
    public Request interceptRequest(Request request, Connection connection) {
        //Log only if the printer is on it to avoid wasting time
        if (Halog.isPrinting()) {
            Halog.d(getClass(), String.format("Request %s: %s \n%s", request.method(), request.url(), request.headers().toString()));
        }
        return request;
    }

    /**
     * Intercepts te
     *
     * @param request  The request intercepted.
     * @param response The response intercepted.
     * @return The response.
     */
    @Override
    public Response interceptResponse(Request request, Response response) {
        //It does not print to avoid wasting time on the object creation
        Response finalResponse = response;
        if (Halog.isPrinting()) {
            Halog.d(getClass(), String.format("Response: %s \n%s", request.url(), request.headers().toString()));
            //Log the response
            try {
                ResponseBody responseBody = response.body();
                String responseBodyString = responseBody.string();
                Halog.d(getClass(), responseBodyString);
                finalResponse = response.newBuilder().body(ResponseBody.create(responseBody.contentType(), responseBodyString)).build();
            } catch (IOException e) {
                Halog.e(getClass(), "Error while parsing the response.");
            }
        }
        return finalResponse;
    }
}
