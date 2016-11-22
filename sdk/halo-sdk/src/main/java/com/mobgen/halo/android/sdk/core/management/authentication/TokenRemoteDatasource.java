package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Token;

/**
 * Remote data source to retrieve the token from the server.
 */
@Keep
public class TokenRemoteDatasource {

    /**
     * Authorization header for the authentication.
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    /**
     * Url to get the client token.
     */
    public static final String URL_GET_CLIENT_TOKEN = "api/oauth/token?_app";
    /**
     * Url to get the device token.
     */
    public static final String URL_GET_USER_TOKEN = "api/oauth/token?_user";

    /**
     * The network client to retrieve network information.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor for the remote data source.
     *
     * @param networkApi The network api.
     */
    public TokenRemoteDatasource(@NonNull HaloNetworkApi networkApi) {
        mClientApi = networkApi;
    }

    /**
     * Retrieves the token.
     *
     * @param credentials The credentials to fetch the token.
     * @return The token obtained.
     * @throws HaloNetException Network exception while trying to bring the token.
     */
    @Nullable
    public Token requestToken(@Nullable Credentials credentials) throws HaloNetException {
        Token token = null;
        if (credentials != null) {
            if (credentials.getLoginType() == Credentials.CLIENT_BASED_LOGIN) { // Client id based
                token = HaloRequest.builder(mClientApi)
                        .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_GET_CLIENT_TOKEN)
                        .method(HaloRequestMethod.POST)
                        .body(HaloBodyFactory.formBody()
                                .add("client_id", credentials.getUsername())
                                .add("client_secret", credentials.getPassword())
                                .add("grant_type", "client_credentials")
                                .build())
                        .build().execute(Token.class);
            } else if (credentials.getLoginType() == Credentials.USER_BASED_LOGIN) { //Device based
                token = HaloRequest.builder(mClientApi)
                        .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_GET_USER_TOKEN)
                        .method(HaloRequestMethod.POST)
                        .header(AUTHORIZATION_HEADER, okhttp3.Credentials.basic(credentials.getUsername(), credentials.getPassword()))
                        .body(HaloBodyFactory.formBody()
                                .add("username", credentials.getUsername())
                                .add("password", credentials.getPassword())
                                .add("grant_type", "password")
                                .build()).build().execute(Token.class);
            }
        }
        return token;
    }
}
