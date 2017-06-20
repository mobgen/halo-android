package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;

import org.json.JSONObject;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

public class PocketRemoteDataSource {

    /**
     * The url to make requests.
     */
    public static final String URL_POCKET_OPERATION = "api/segmentation/identified-pocket/self?";
    public static final String URL_POCKET_OPERATION_SAVE = "api/segmentation/identified-pocket/";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor datasource.
     *
     * @param clientApi The client api.
     */
    public PocketRemoteDataSource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }


    @NonNull
    public Pocket getPocket(@NonNull String referenceFilter) throws HaloNetException, HaloParsingException {

        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_POCKET_OPERATION + referenceFilter)
                .method(HaloRequestMethod.GET)
                .build().execute(Pocket.class);
    }

    @NonNull
    public Pocket savePocket(@NonNull Pocket pocket) throws HaloNetException, HaloParsingException {

        JSONObject jsonObject = null;
        try {
            String paramsSerialized = Pocket.serialize(pocket, Halo.instance().framework().parser());
            jsonObject = new JSONObject(paramsSerialized);
        } catch (Exception e) {
            throw new HaloParsingException(e.toString(), e);
        }
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_POCKET_OPERATION_SAVE)
                .method(HaloRequestMethod.PUT)
                .body(HaloBodyFactory.jsonObjectBody(jsonObject))
                .build().execute(Pocket.class);
    }
}
