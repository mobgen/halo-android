package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.interceptors.RequestResponseInterceptor;
import com.mobgen.halo.android.framework.network.sessions.HaloSession;
import com.mobgen.halo.android.framework.network.sessions.HaloSessionManager;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Session;
import com.mobgen.halo.android.sdk.core.management.models.Token;

import java.io.IOException;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Challenge;
import okhttp3.Connection;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * The oauth authentication provider that provides the oauth information for the current request.
 */
public class HaloAuthenticator implements Authenticator {

    /**
     * The session index name. This is reserved.
     */
    public static final String HALO_SESSION_NAME = "halo-session";

    /**
     * Authorization header for the authentication.
     */
    public static final String AUTHENTICATION_HEADER = "Authorization";

    /**
     * The core middleware.
     */
    @NonNull
    private HaloSessionManager mSessionManager;
    /**
     * The framework.
     */
    @NonNull
    private final HaloFramework mFramework;

    /**
     * The client credentials.
     */
    private Credentials mClientCredentials;

    /**
     * The device credentials.
     */
    private Credentials mUserCredentials;

    /**
     * The management api.
     */
    private HaloManagerApi mManagementApi;

    /**
     * Creates the oauth provider given a client id and a client secret.
     *
     * @param framework      The framework.
     * @param managerApi     The manager api.
     * @param sessionManager The session manager.
     * @param credentials    The credentials for the authentication.
     */
    public HaloAuthenticator(@NonNull HaloFramework framework,
                             @NonNull HaloManagerApi managerApi,
                             @NonNull HaloSessionManager sessionManager,
                             @NonNull Credentials credentials) {
        AssertionUtils.notNull(framework, "framework");
        AssertionUtils.notNull(managerApi, "managementApi");
        AssertionUtils.notNull(sessionManager, "sessionManager");
        AssertionUtils.notNull(credentials, "credentials");
        mFramework = framework;
        mManagementApi = managerApi;
        mSessionManager = sessionManager;
        mClientCredentials = credentials;
        attachToFramework();
    }

    @Override
    public synchronized Request authenticate(Route route, Response response) throws IOException {
        HaloSession session = mSessionManager.getSession(HALO_SESSION_NAME);
        try {
            if (session == null || session.isSessionExpired() || session.mayBeServerExpired()) {
                flushSession();
                //Do auth
                Token token = null;
                if(!response.request().url().toString().endsWith(TokenRemoteDatasource.URL_GET_CLIENT_TOKEN) &&
                        !response.request().url().toString().endsWith(TokenRemoteDatasource.URL_GET_USER_TOKEN)) {
                    token = requestToken();
                    if (token != null) {
                        session = new Session(token);
                        //recover login if there is a halo social api
                        if(Halo.instance().getCore().haloSocial()!=null) {
                            //recover account if exist
                            Halo.instance().getCore().haloSocial().recoverAccount();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Halog.e(getClass(), "The token could not be obtained. " + e.getMessage());
            throw e;
        }

        //The request authentication finished successfully
        if (session != null) {
            //Store the session
            mSessionManager.setSession(HALO_SESSION_NAME, session);
            return response.request()
                    .newBuilder()
                    .header(AUTHENTICATION_HEADER, session.getSessionAuthentication())
                    .build();
        }
        //Do not reconnect, we can not authenticate with the current token
        return null;
    }

    /**
     * Requests the token based on the login type.
     *
     * @return The token retrieved or null if there is no available token.
     * @throws HaloNetException Error while retrieving a token.
     */
    private Token requestToken() throws HaloNetException {
        final Token[] token = new Token[1];
        if (mManagementApi != null) {
            mManagementApi.requestToken(getCredentials())
                    .threadPolicy(Threading.SAME_THREAD_POLICY)
                    .bypassHaloReadyCheck()
                    .execute(new CallbackV2<Token>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<Token> result) {
                            token[0] = result.data();
                        }
                    });
        }
        return token[0];
    }

    /**
     * Attaches the OAuth authenticator system into the networking middleware.
     */
    private void attachToFramework() {
        OkHttpClient originalHttp = mFramework.network().client().ok();
        OkHttpClient.Builder newBuilder = originalHttp.newBuilder().authenticator(this);
        newBuilder.interceptors().add(0, new SessionInterceptor(mSessionManager));
        mFramework.network().client().overrideOk(newBuilder);
    }

    /**
     * Sets the credentials to log in.
     *
     * @param credentials The credentials.
     */
    public synchronized void setCredentials(@NonNull Credentials credentials) {
        AssertionUtils.notNull(credentials, "credentials");
        if (credentials.getLoginType() == Credentials.CLIENT_BASED_LOGIN) {
            mClientCredentials = credentials;
        } else if (credentials.getLoginType() == Credentials.USER_BASED_LOGIN) {
            mUserCredentials = credentials;
        }
        flushSession();
    }

    /**
     * Logouts the current scaled device.
     */
    public void logout() {
        mUserCredentials = null;
        flushSession();
    }

    /**
     * Provides the credentials.
     *
     * @return The credentials.
     */
    @NonNull
    public synchronized Credentials getCredentials() {
        Credentials credentials;
        if (mUserCredentials != null) {
            credentials = mUserCredentials;
        } else {
            credentials = mClientCredentials;
        }
        return credentials;
    }

    /**
     * Flushes the current session from the session manager.
     */
    public void flushSession() {
        mSessionManager.flushSession(HaloAuthenticator.HALO_SESSION_NAME);
    }

    /**
     * Provides the session manager.
     *
     * @return The session manager.
     */
    public HaloSessionManager getSessionManager() {
        return mSessionManager;
    }

    /**
     * Interceptor that adds the session to the okhttp.
     */
    private static class SessionInterceptor extends RequestResponseInterceptor {

        /**
         * The session manager.
         */
        private HaloSessionManager mSessionManager;

        /**
         * The session interceptor.
         *
         * @param sessionManager The session manager.
         */
        public SessionInterceptor(@NonNull HaloSessionManager sessionManager) {
            mSessionManager = sessionManager;
        }

        @Override
        public Request interceptRequest(@NonNull Request request, @Nullable Connection connection) {
            HaloSession session = mSessionManager.getSession(HaloAuthenticator.HALO_SESSION_NAME);
            //Add the authorization header
            Request finalRequest = request;
            if (session != null && request.header(AUTHENTICATION_HEADER) == null) {
                finalRequest = request.newBuilder().addHeader(AUTHENTICATION_HEADER, session.getSessionAuthentication()).build();
            }
            return finalRequest;
        }
    }
}
