package com.mobgen.halo.android.social.providers;

/**
 * Error produced when a social network is not available.
 */

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;
@Keep
public class SocialNotAvailableException extends Exception {

    /**
     * Constructor for the social network not available.
     *
     * @param detailMessage A detailed message.
     */
    @Api(2.1)
    public SocialNotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor for the social network not available.
     *
     * @param detailMessage Detailed message.
     * @param throwable     The error that was a cause.
     */
    @Api(2.1)
    public SocialNotAvailableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
