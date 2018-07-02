package com.mobgen.halo.android.sdk.core.internal.network;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Contains all the urls for the apis that will be used in halo.
 */
@Keep
public final class HaloNetworkConstants {

    //ENDPOINTS
    /**
     * The halo endpoint id.
     */
    @Api(1.0)
    public static final String HALO_ENDPOINT_ID = "halo:endpoint";
    /**
     * The production url for the SDK.
     */
    @Api(1.0)
    public static final String HALO_PROD_ENDPOINT_URL = "https://halo.mobgen.com";

    /**
     * Pinning sha for the https protocol.
     */
    @Api(1.0)
    public static final String HALO_SHA_PINNING = "sha1/Pg7klL10qanbSkrRLnCKqxMqRaA=";
    /**
     * New certificate for the 2017 certificate of MOBGEN.
     */
    @Api(2.1)
    public static final String HALO_SHA_PINNING_CERT2017 = "sha256/HcHXoIBbE2vePMjx3LVYxkaJ6zsanFWq3ABXzEHm0z0=";
    /**
     * New certificate for the 2018 certificate of MOBGEN.
     */
    @Api(2.6)
    public static final String HALO_SHA_PINNING_CERT2018 = "sha256/4973NGEUVzxEnad03PaQItAaK0TgZMUPxAU6XWCjtGY=";

    /**
     * Private empty constructor.
     */
    private HaloNetworkConstants() {
        //Private constructor to avoid instances.
    }
}
