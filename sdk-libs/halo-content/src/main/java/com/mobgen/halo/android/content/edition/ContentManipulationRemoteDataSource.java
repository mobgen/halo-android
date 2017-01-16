package com.mobgen.halo.android.content.edition;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloEditContentOptions;
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
 *  Remote data source for content manipulation.
 */
public class ContentManipulationRemoteDataSource {

    /**
     * The url to make requests.
     */
    public static final String URL_CONTENT_OPERATION = "api/generalcontent/instance/";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor datasource.
     *
     * @param clientApi The client api.
     */
    public ContentManipulationRemoteDataSource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Request to add a new general content instance.
     * @param operation The halo request operation type.
     * @param haloEditContentOptions The general content instance to add.
     * @return The HaloContentInstance created.
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public HaloContentInstance addContent(@NonNull HaloRequestMethod operation, @NonNull HaloEditContentOptions haloEditContentOptions) throws HaloNetException, HaloParsingException {

        JSONObject jsonObject = null;
        try {
            String paramsSerialized = HaloEditContentOptions.serialize(haloEditContentOptions, Halo.instance().framework().parser());
            jsonObject = new JSONObject(paramsSerialized);
        } catch (Exception e) {
            throw new HaloParsingException(e.toString(), e);
        }
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_CONTENT_OPERATION)
                .method(operation)
                .body(HaloBodyFactory.jsonObjectBody(jsonObject))
                .build().execute(HaloContentInstance.class);
    }

    /**
     * Request to update a given general content instance.
     * @param operation The halo request operation type.
     * @param haloEditContentOptions The general content instance to add.
     * @return The HaloContentInstance updated.
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public HaloContentInstance updateContent(@NonNull HaloRequestMethod operation, @NonNull HaloEditContentOptions haloEditContentOptions) throws HaloNetException, HaloParsingException {

        JSONObject jsonObject = null;
        try {
            String paramsSerialized = HaloEditContentOptions.serialize(haloEditContentOptions, Halo.instance().framework().parser());
            jsonObject = new JSONObject(paramsSerialized);
        } catch (Exception e) {
            throw new HaloParsingException(e.toString(), e);
        }
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_CONTENT_OPERATION + haloEditContentOptions.getItemId())
                .method(operation)
                .body(HaloBodyFactory.jsonObjectBody(jsonObject))
                .build().execute(HaloContentInstance.class);
    }

    /**
     * Request to remove a new general content instance.
     * @param operation The halo request operation type.
     * @param haloEditContentOptions The general content instance to remove.
     * @return The HaloContentInstance deleted.
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public HaloContentInstance deleteContent(@NonNull HaloRequestMethod operation, @NonNull HaloEditContentOptions haloEditContentOptions) throws HaloNetException, HaloParsingException {

        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_CONTENT_OPERATION + haloEditContentOptions.getItemId())
                .method(operation)
                .build().execute(HaloContentInstance.class);
    }
}
