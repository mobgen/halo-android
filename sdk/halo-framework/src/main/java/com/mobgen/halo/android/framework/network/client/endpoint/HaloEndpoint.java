package com.mobgen.halo.android.framework.network.client.endpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Represents the HALO endpoint to perform all the requests.
 */
public class HaloEndpoint {

    /**
     * The name of the endpoint.
     */
    private final String mEndpointId;

    /**
     * The HALO endpoint used for all the requests.
     */
    private final String mEndpoint;

    /**
     * The certificate for pinning in sha.
     */
    private final String[] mCertificatePinningSHA;

    /**
     * Tells if the ssl pinning is enabled.
     */
    private boolean mIsPinningEnabled;

    /**
     * The current endpoint supporting certificate pinning for HALO.
     *
     * @param endpointId            The id of the current endpoint.
     * @param endpoint              The endpoint.
     * @param certificatePinningSHA The certificate pinning.
     */
    @Api(2.0)
    public HaloEndpoint(@NonNull String endpointId, @NonNull String endpoint, @Nullable String... certificatePinningSHA) {
        mEndpointId = endpointId;
        mEndpoint = endpoint;
        mCertificatePinningSHA = certificatePinningSHA;
        mIsPinningEnabled = true;
    }

    /**
     * The current endpoint supporting certificate pinning for HALO.
     *
     * @param endpointId The id of the current endpoint.
     * @param endpoint   The endpoint.
     */
    @Api(2.0)
    public HaloEndpoint(@NonNull String endpointId, @NonNull String endpoint) {
        this(endpointId, endpoint, new String[]{});
    }

    /**
     * Provides the endpoint information.
     * @return Provides the endpoint.
     */
    @Api(2.0)
    @NonNull
    public String getEndpoint() {
        return mEndpoint;
    }

    /**
     * The certificate pinning string value.
     *
     * @return The pinning value.
     */
    @Api(2.0)
    @NonNull
    public String[] getCertificatePinningSHA() {
        return mCertificatePinningSHA;
    }

    /**
     * Provides the id of the endpoint.
     *
     * @return The id of the endpoint.
     */
    @Api(2.0)
    @NonNull
    public String getEndpointId() {
        return mEndpointId;
    }

    /**
     * True if the ssl pinning is enabled, false otherwise.
     * @return True if the pinning is enabled. False otherwise.
     */
    @Api(2.0)
    public boolean isSslPinningEnabled(){
        return mIsPinningEnabled && mCertificatePinningSHA != null && mCertificatePinningSHA.length > 0;
    }

    /**
     * Disables the ssl pinning for this endpoint instance.
     */
    public void disablePinning() {
        mIsPinningEnabled = false;
    }

    /**
     * The request builder that allows to concat the endpoint. The api call should be
     * in the way: /something/something...
     *
     * @param apiCall The api call.
     * @return The full request path.
     */
    @Api(2.0)
    public String buildUrl(@NonNull String apiCall) {
        return mEndpoint + "/" + apiCall;
    }

    @Override
    public int hashCode() {
        return mEndpointId.hashCode() + 33;
    }

    @Override
    public boolean equals(Object o) {
        //Two endpoints are the same if they have the same name.
        if (o instanceof HaloEndpoint) {
            HaloEndpoint endpoint = (HaloEndpoint) o;
            return this == o || endpoint.mEndpointId.equals(mEndpointId);
        }
        return super.equals(o);
    }
}
