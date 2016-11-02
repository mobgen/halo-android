package com.mobgen.halo.android.framework.network.sessions;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * A session represents the user logged in and the token used to make all the requests. It is not allowed
 * to have a session without a token and if the token expires the session have to be removed. It also gives the possibility
 * to add some session objects to the current session.
 */
public abstract class HaloSession {

    /**
     * Provides the session token string.
     *
     * @return The session token.
     */
    @Api(1.0)
    public abstract String getSessionAuthentication();

    /**
     * Provides if the session is expired.
     *
     * @return True if the session is expired, false otherwise.
     */
    @Api(1.0)
    public abstract boolean isSessionExpired();

    /**
     * Determines that a token can be expired from the server side.
     *
     * @return True if expired, false otherwise.
     */
    @Api(1.3)
    public abstract boolean mayBeServerExpired();

    /**
     * Provides the refresh token.
     *
     * @return The refresh token.
     */
    @Api(1.2)
    @NonNull
    public abstract String getRefreshToken();
}
