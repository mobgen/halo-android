package com.mobgen.halo.android.framework.network.client.endpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;

import java.util.HashMap;
import java.util.Map;

import okhttp3.CertificatePinner;

/**
 * It is a map enclosing all the endpoints available for halo.
 */
public class HaloEndpointCluster {

    /**
     * The cluster with the endpoints.
     */
    private final Map<String, HaloEndpoint> mCluster;

    /**
     * Creates the cluster and adds all the endpoints to the cluster.
     *
     * @param endpoints The endpoint.
     */
    @Api(2.0)
    public HaloEndpointCluster(@NonNull HaloEndpoint... endpoints) {
        mCluster = new HashMap<>();
        for (HaloEndpoint endpoint : endpoints) {
            mCluster.put(endpoint.getEndpointId(), endpoint);
        }
    }

    /**
     * Provides the endpoint for the given id.
     *
     * @param endpointId The endpoint id.
     * @return The endpoint found.
     */
    @NonNull
    @Api(2.0)
    public HaloEndpoint getEndpoint(@NonNull String endpointId) {
        HaloEndpoint endpoint = mCluster.get(endpointId);
        if (endpoint == null) {
            throw new HaloConfigurationException("Error while retrieving the endpoint with name: " + endpointId + ". Remember to register it before getting it.");
        }
        return endpoint;
    }


    /**
     * Builds a the pinner that can be added to okhttp only if there are items to pin.
     *
     * @return The pinner or null if there are no pinnings in the endpoints.
     */
    @Nullable
    @Api(2.0)
    public CertificatePinner buildCertificatePinner() {
        CertificatePinner.Builder certificatePinner = new CertificatePinner.Builder();
        boolean hasPinning = false;
        for (Map.Entry<String, HaloEndpoint> endpointEntry : mCluster.entrySet()) {
            HaloEndpoint endpoint = endpointEntry.getValue();
            if (endpoint.isSslPinningEnabled()) {
                certificatePinner.add(endpoint.getEndpoint().replace("https://", ""), endpoint.getCertificatePinningSHA());
                hasPinning = true;
            }
        }
        return hasPinning ? certificatePinner.build() : null;
    }

    /**
     * Registers a new endpoint on the client.
     *
     * @param endpoint The endpoint registered.
     */
    @Api(2.0)
    public void registerEndpoint(@NonNull HaloEndpoint endpoint) {
        mCluster.put(endpoint.getEndpointId(), endpoint);
    }
}
