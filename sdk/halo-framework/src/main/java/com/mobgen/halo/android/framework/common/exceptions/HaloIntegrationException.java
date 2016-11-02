package com.mobgen.halo.android.framework.common.exceptions;

import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Error with an integration in halo.
 */
public class HaloIntegrationException extends Exception {

    /**
     * The message related to the integration.
     */
    private String mIntegrationMessage;

    /**
     * The status code of the integration.
     */
    private int mIntegrationStatusCode;

    /**
     * Constructor for the integration exception.
     *
     * @param integrationMessage The integration message.
     * @param integrationCode The integration status code.
     * @param message The message.
     * @param e       The exception.
     */
    @Api(2.0)
    public HaloIntegrationException(@Nullable String integrationMessage, int integrationCode, @Nullable String message, @Nullable Exception e) {
        super(message, e);
        mIntegrationMessage = integrationMessage;
        mIntegrationStatusCode = integrationCode;
    }

    /**
     * Constructor for the integration exception.
     *
     * @param message The message.
     * @param e       The exception.
     */
    @Api(2.0)
    public HaloIntegrationException(@Nullable String message, @Nullable Exception e) {
        super(message, e);
    }

    /**
     * Constructor for the halo integration exception.
     *
     * @param message The message.
     */
    @Api(2.0)
    public HaloIntegrationException(@Nullable String message) {
        super(message);
    }

    /**
     * The integration message.
     * @return The integration message.
     */
    @Api(2.0)
    @Nullable
    public String integrationMessage() {
        return mIntegrationMessage;
    }

    /**
     * The integration error code.
     * @return The integration error code.
     */
    @Api(2.0)
    public int statusCode() {
        return mIntegrationStatusCode;
    }
}
