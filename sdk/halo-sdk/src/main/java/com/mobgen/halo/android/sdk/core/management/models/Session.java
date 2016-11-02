package com.mobgen.halo.android.sdk.core.management.models;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.network.sessions.HaloSession;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Session that contains the device information related to the authentication. If there is a session
 * it means there is a token active, once this token starts giving authentication errors it means
 * the session is not valid anymore and a new authentication request must be done.
 *
 * @see Credentials
 * @see Token
 */
public class Session extends HaloSession {

    /**
     * The window where it is accepted to have multiple requests without expiring the token.
     */
    private static final long MULTIPLE_REQUEST_WINDOW = TimeUnit.SECONDS.toMillis(10);

    /**
     * The authentication challenge.
     */
    private Token mToken;

    /**
     * The session constructor for the halo stuff.
     *
     * @param token The token challenge on this session.
     */
    @Api(1.0)
    public Session(@NonNull Token token) {
        mToken = token;
    }

    @Api(1.0)
    @Override
    @NonNull
    public String getSessionAuthentication() {
        return mToken.getAuthorization();
    }

    @Api(1.0)
    @Override
    public boolean isSessionExpired() {
        Date now = new Date();
        long expirationTime = mToken.getReceivedDate().getTime() + mToken.getExpiresIn();
        return now.getTime() - expirationTime > 0;
    }

    @Override
    public boolean mayBeServerExpired() {
        Date now = new Date();
        return (now.getTime() - mToken.getReceivedDate().getTime()) > MULTIPLE_REQUEST_WINDOW;
    }

    @NonNull
    @Override
    public String getRefreshToken() {
        return mToken.getRefreshToken();
    }
}
