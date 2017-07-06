package com.mobgen.halo.android.content.search;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.spec.HaloContentNetwork;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.body.HaloMediaType;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;

import java.io.IOException;
import java.util.List;

/**
 * @hide This data source makes a query to halo to request the instances based
 * on the query provided.
 */
public class ContentSearchRemoteDatasource {

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor to build the search datasource.
     *
     * @param clientApi The client api.
     */
    public ContentSearchRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Finds data by the query.
     *
     * @param query The query.
     * @return The resulting data.
     * @throws HaloNetException Error while requesting data.
     */
    public Paginated<HaloContentInstance> findByQuery(@NonNull SearchQuery query) throws HaloNetException {
        String jsonParsed;
        try {
            jsonParsed = query.serializerFrom(mClientApi.framework().parser()).convert(query);
        } catch (IOException e) {
            throw new HaloNetParseException("Exception while parsing the options", e);
        }
        //The request
        HaloRequest request = HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, HaloContentNetwork.URL_SEARCH_INSTANCES)
                .method(HaloRequestMethod.POST)
                .cacheHeader(query.serverCache())
                .body(HaloBodyFactory.stringBody(HaloMediaType.APPLICATION_JSON, jsonParsed))
                .build();

        //Bring the result in different ways when paginated and when it is not
        Paginated<HaloContentInstance> instances;
        //TODO remove paginated amb.
        if (query.isPaginated()) {
            instances = request.execute(new TypeReference<Paginated<HaloContentInstance>>() {
            });
        } else {
            instances = new Paginated<>(request.execute(new TypeReference<List<HaloContentInstance>>() {
            }));
        }
        return instances;
    }
}
