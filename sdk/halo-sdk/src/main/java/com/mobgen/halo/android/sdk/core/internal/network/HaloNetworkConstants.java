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
    public static final String HALO_PROD_ENDPOINT_URL = "https://halo-db.com";

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
     * Certificate for the aws root ca 1.
     */
    @Api(2.8)
    public static final String HALO_SHA_PINNING_CERT_AWS_ROOT_CA_1 = "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=";
    /**
     * Certificate for the aws root ca 2.
     */
    @Api(2.8)
    public static final String HALO_SHA_PINNING_CERT_AWS_ROOT_CA_2 = "sha256/f0KW/FtqTjs108NpYj42SrGvOB2PpxIVM8nWxjPqJGE=";
    /**
     * Certificate for the aws root ca 3.
     */
    @Api(2.8)
    public static final String HALO_SHA_PINNING_CERT_AWS_ROOT_CA_3 = "sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";
    /**
     * Certificate for the aws root ca 0.
     */
    @Api(2.8)
    public static final String HALO_SHA_PINNING_CERT_AWS_ROOT_CA_0 = "sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=";

    /**
     * Private empty constructor.
     */
    private HaloNetworkConstants() {
        //Private constructor to avoid instances.
    }
}
