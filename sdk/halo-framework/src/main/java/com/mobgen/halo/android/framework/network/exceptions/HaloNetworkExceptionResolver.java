package com.mobgen.halo.android.framework.network.exceptions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * The exception resolver maps the possible errors that can be produced on the network layer to
 * different exceptions that are more meaningful for the sdk user.
 */
public class HaloNetworkExceptionResolver {

    /**
     * Resolves an input/output exception to another exception.
     *
     * @param exception          The io exception produced when the exception can not reach the server.
     * @param request            The request that needs to be resolved.
     * @param isNetworkConnected Checks if the network is connected.
     * @return The exception mapped.
     */
    @NonNull
    @Api(2.0)
    public HaloNetException resolve(@NonNull Exception exception, @Nullable Request request, boolean isNetworkConnected) {
        HaloNetException exceptionMapped;
        String url = request != null ? request.url().toString() : null;
        if (!isNetworkConnected) {
            exceptionMapped = new HaloConnectionException("The device has not connectivity to reach the server on " + url, exception);
        } else {
            exceptionMapped = new HaloUnknownServerException("An unknown exception has been produced when performing the request: " + exception.getMessage(), exception);
        }
        return exceptionMapped;
    }

    /**
     * Resolves a not successful network operation.
     *
     * @param response           The response we will use to map.
     * @return The exception mapped.
     * @throws IOException Error while taking the body value for an internal error.
     */
    @NonNull
    @Api(2.0)
    public HaloNetException resolve(@NonNull Response response) throws IOException {
        HaloNetException exceptionMapped;
        if (response.code() == 404) {
            exceptionMapped = new HaloNotFoundException("The resource on " + response.request().url() + " was not found with code " + response.code() + " .");
        } else if (response.code() == 401 || response.code() == 403) {
            exceptionMapped = new HaloAuthenticationException("You don't have permissions to access on " + response.request().url().toString() + " with code " + response.code());
        } else if ((response.code() >= 500 && response.code() < 600) || response.code() == 400) {
            exceptionMapped = new HaloServerException("Internal server error on " + response.request().url().toString() + " with code " + response.code(), response.newBuilder().build().body().string(), response.code());
        } else {
            exceptionMapped = new HaloUnknownServerException("An unknown exception has been produced when performing the request on " + response.request().url().toString() + " with code " + response.code());
        }
        return exceptionMapped;
    }
}
