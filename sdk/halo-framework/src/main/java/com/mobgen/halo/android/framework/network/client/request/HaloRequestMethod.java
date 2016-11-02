package com.mobgen.halo.android.framework.network.client.request;

/**
 * The Http allowed method types.
 */
public enum HaloRequestMethod {
    /**
     * GET request, no body, response allowed.
     */
    GET,
    /**
     * POST request, allow body and response.
     */
    POST,
    /**
     * PUT request, allow body, no response.
     */
    PUT,
    /**
     * DELETE request, allow body, no response.
     */
    DELETE,
    /**
     * PATCH request, allow body, response allowed.
     */
    PATCH
}