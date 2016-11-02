package com.mobgen.halo.android.social.login;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.social.models.IdentifiedUser;

/**
 * Login remote source
 */
public class LoginRemoteDatasource {

    /**
     * The url to login.
     */
    public static final String URL_LOGIN = "api/segmentation/identified/login";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor datasource to login with a social network.
     * @param clientApi The client api.
     */
    public LoginRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Fetches the user identity
     *
     * @param socialApiName The social name.
     * @param socialToken The social network token.
     * @param alias The device alias.
     * @throws HaloNetException Networking exception.
     * @return The user identity as IdentifiedUser
     */
    @NonNull
    public IdentifiedUser loginSocial( @NonNull String socialApiName, @NonNull String socialToken, @NonNull String alias) throws HaloNetException {

        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_LOGIN)
                .method(HaloRequestMethod.POST)
                .body(HaloBodyFactory.formBody()
                        .add("network", socialApiName)
                        .add("token", socialToken)
                        .add("deviceId", alias)
                        .build())
                .build().execute(IdentifiedUser.class);
    }

    /**
     * Fetches the user identity
     *
     * @param username The user email.
     * @param password The password.
     * @param alias The device alias.
     * @throws HaloNetException Networking exception.
     * @return The user identity as IdentifiedUser
     */
    @NonNull
    public IdentifiedUser login(String username, String password, @NonNull String alias) throws HaloNetException {

        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_LOGIN)
                .method(HaloRequestMethod.POST)
                .body(HaloBodyFactory.formBody()
                        .add("network","halo")
                        .add("email", username)
                        .add("password", password)
                        .add("deviceId", alias)
                        .build())
                .build().execute(IdentifiedUser.class);
    }
}
