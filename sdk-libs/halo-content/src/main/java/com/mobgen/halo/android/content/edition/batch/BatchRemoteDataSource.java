package com.mobgen.halo.android.content.edition.batch;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.BatchOperationResults;
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
 * Remote data source for batch content manipulation.
 */
public class BatchRemoteDataSource {

    /**
     * The url to make requests.
     */
    public static final String URL_CONTENT_OPERATION = "api/generalcontent/instance/batch";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor datasource.
     *
     * @param clientApi The client api.
     */
    public BatchRemoteDataSource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Request to perfom batch operations.
     *
     * @param batchOperations The batch operation to perfom.
     * @return The batch remote operation result.
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public BatchOperationResults batchOperation(@NonNull BatchOperations batchOperations) throws HaloNetException, HaloParsingException {

        JSONObject jsonObject = null;
        try {
            String paramsSerialized = BatchOperations.serialize(batchOperations, Halo.instance().framework().parser());
            jsonObject = new JSONObject(paramsSerialized);
        } catch (Exception e) {
            throw new HaloParsingException(e.toString(), e);
        }
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_CONTENT_OPERATION)
                .method(HaloRequestMethod.POST)
                .body(HaloBodyFactory.jsonObjectBody(jsonObject))
                .build().execute(BatchOperationResults.class);
    }

}
