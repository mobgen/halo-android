package com.mobgen.halo.android.social.register;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.Register;

import org.json.JSONObject;

/**
 * Sign in remote source
 */
public class RegisterRemoteDatasource {

    /**
     * The url to sign in.
     */
    public static final String URL_LOGIN = "api/segmentation/identified/register";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor datasource to login with a social network.
     *
     * @param clientApi The client api.
     */
    public RegisterRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }


    /**
     * Register a user on Halo
     *
     * @param haloAuthProfile The auth profile.
     * @param haloUserProfile The user profile.
     * @return The user identity as IdentifiedUser
     * @throws HaloNetException Networking exception.
     * @throws HaloParsingException Parsing exception.
     */
    @NonNull
    public HaloUserProfile register(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) throws HaloNetException, HaloParsingException {
        JSONObject jsonObject = null;
        try {
            Register register = new Register(haloAuthProfile, haloUserProfile);
            String registerSerialized = register.serialize(register, Halo.instance().framework().parser());
            jsonObject = new JSONObject(registerSerialized);
        } catch (Exception e) {
            throw new HaloParsingException(e.toString(), e);
        }
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_LOGIN)
                .method(HaloRequestMethod.POST)
                .body(HaloBodyFactory.jsonObjectBody(jsonObject))
                .build().execute(HaloUserProfile.class);
    }
}
