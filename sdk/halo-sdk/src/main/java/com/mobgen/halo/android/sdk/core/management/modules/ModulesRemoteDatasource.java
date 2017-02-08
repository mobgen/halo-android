package com.mobgen.halo.android.sdk.core.management.modules;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.List;

/**
 * The remote data source to retrieve the modules for a given
 * application.
 */
@Keep
public class ModulesRemoteDatasource {

    /**
     * The url to retrieve the modules.
     */
    public static final String URL_GET_MODULES = "api/generalcontent/module?skip=true";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor to retrieve the modules.
     * @param clientApi The client api.
     */
    public ModulesRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * The modules to retrieve.
     * @return The modules obtained.
     */
    @NonNull
    public List<HaloModule> getModules(boolean withFields) throws HaloNetException {

        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_GET_MODULES +"&withFields=" + withFields)
                .method(HaloRequestMethod.GET)
                .build().execute(new TypeReference<List<HaloModule>>() {
                });
    }
}
