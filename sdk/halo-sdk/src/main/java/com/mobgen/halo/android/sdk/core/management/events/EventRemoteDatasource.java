package com.mobgen.halo.android.sdk.core.management.events;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.models.HaloEvent;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;

import org.json.JSONObject;

/**
 * Created by f.souto.gonzalez on 02/06/2017.
 */

/**
 * Remote data source for tracking events.
 *
 */
public class EventRemoteDatasource {

    /**
     * Create a analytic tracking event.
     */
    private static final String URL_CREATE_EVENT = "api/events/daily/create";


    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Creates a remote data source for the event traker.
     *
     * @param clientApi The remote data source.
     */
    public EventRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    @NonNull
    public HaloEvent sendEvent(@NonNull HaloEvent haloEvent) throws HaloNetException,HaloParsingException {

        JSONObject jsonObject = null;
        try {
            String paramsSerialized = HaloEvent.serialize(haloEvent, Halo.instance().framework().parser());
            jsonObject = new JSONObject(paramsSerialized);
        } catch (Exception e) {
            throw new HaloParsingException(e.toString(), e);
        }

        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_CREATE_EVENT)
                .method(HaloRequestMethod.POST)
                .body(HaloBodyFactory.jsonObjectBody(jsonObject))
                .build().execute(HaloEvent.class);
    }

}
