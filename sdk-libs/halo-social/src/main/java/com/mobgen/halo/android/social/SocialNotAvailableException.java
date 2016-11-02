package com.mobgen.halo.android.social;

/**
 * Error produced when a social network is not available.
 */
public class SocialNotAvailableException extends Exception {

    /**
     * Constructor for the social network not available.
     *
     * @param detailMessage A detailed message.
     */
    public SocialNotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor for the social network not available.
     *
     * @param detailMessage Detailed message.
     * @param throwable     The error that was a cause.
     */
    public SocialNotAvailableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
