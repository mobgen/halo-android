package com.mobgen.halo.android.sdk.core.management.version;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;

/**
 * Provides the version of the server and allows us to check against it and
 * take some decisions.
 */
@Keep
public class VersionRemoteDatasource {

    /**
     * The url of the server version.
     */
    public static final String URL_VERSION = "api/authentication/version/current";

    /**
     * The client Api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor for the version checker.
     *
     * @param clientApi The client api.
     */
    public VersionRemoteDatasource(HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Provides the latest version available in the server.
     *
     * @return The latest version.
     * @throws HaloNetException Error while performing the network request.
     */
    @Nullable
    public HaloServerVersion getServerVersion() throws HaloNetException {
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_VERSION)
                .method(HaloRequestMethod.GET)
                .build().execute(new TypeReference<HaloServerVersion>() {
                });
    }
}
