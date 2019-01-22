package com.mobgen.halo.android.framework.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;

/**
 * Networking api that contains all items needed to make calls on the network.
 */
public class HaloNetworkApi {

    /**
     * The halo framework instance that contains this api.
     */
    private HaloFramework mFramework;

    /**
     * The networking client.
     */
    private HaloNetClient mClient;

    /**
     * Constructs the networking api.
     *
     * @param framework The framework.
     * @param client    The halo client.
     */
    protected HaloNetworkApi(@NonNull HaloFramework framework, @NonNull HaloNetClient client) {
        mFramework = framework;
        mClient = client;
    }

    /**
     * Creates a new network api given a configuration.
     *
     * @param framework     The framework.
     * @param configuration The configuration.
     * @return The network api.
     */
    @Api(1.3)
    @NonNull
    public static HaloNetworkApi newNetworkApi(@NonNull HaloFramework framework, @NonNull HaloConfig configuration) {
        HaloNetClient client = new HaloNetClient(configuration.getContext(), configuration.getOkHttpBuilder(), configuration.getEndpointCluster(), configuration.getEnableKitKatCertificate());
        return new HaloNetworkApi(framework, client);
    }

    /**
     * Provides the client instance.
     *
     * @return The client instance.
     */
    @Api(1.0)
    @NonNull
    public HaloNetClient client() {
        return mClient;
    }

    /**
     * Provides the context.
     *
     * @return The application context.
     */
    @Api(1.1)
    @NonNull
    public Context context() {
        return mFramework.context();
    }

    /**
     * Provides the halo framework instance.
     *
     * @return The framework instance.
     */
    @Api(1.3)
    public HaloFramework framework() {
        return mFramework;
    }

    /**
     * Provides the request url for the given call.
     *
     * @param apiCall    The url.
     * @param endpointId The endpoint id.
     * @return The full url.
     */
    @Api(1.0)
    @NonNull
    public String requestUrl(@NonNull String endpointId, @NonNull String apiCall) {
        //Build the request for the given endpoint
        return mClient.endpoints().getEndpoint(endpointId).buildUrl(apiCall);
    }
}
