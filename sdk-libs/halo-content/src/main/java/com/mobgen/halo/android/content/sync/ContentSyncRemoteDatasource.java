package com.mobgen.halo.android.content.sync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.HaloInstanceSync;
import com.mobgen.halo.android.content.spec.HaloContentNetwork;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * @hide Remote datasource that request synced instances.
 */
public class ContentSyncRemoteDatasource {

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor to build the search datasource.
     *
     * @param clientApi The client api.
     */
    public ContentSyncRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Syncs a module and provides the result.
     *
     * @param moduleToSync The module to sync.
     * @param locale       Locale.
     * @param fromSync     The last synced date.
     * @return The instances to sync.
     * @throws HaloNetException Error while requesting the module.
     */
    @NonNull
    public HaloInstanceSync syncModule(int cacheTime, @NonNull String moduleToSync, @Nullable String locale, @Nullable Date fromSync) throws HaloNetException {
        long millis = System.currentTimeMillis();
        HaloInstanceSync syncInstance = createSyncRequest(cacheTime, fromSync, moduleToSync, locale, false).execute(HaloInstanceSync.class);
        boolean isFirstSync = fromSync == null;
        if (isFirstSync) {
            HaloRequest request = createSyncRequest(cacheTime, syncInstance.getSyncDate(), moduleToSync, locale, false);
            HaloInstanceSync fromCacheSyncInstance = request.execute(HaloInstanceSync.class);
            syncInstance.mergeWith(fromCacheSyncInstance);
        }
        Halog.d(getClass(), Long.valueOf(System.currentTimeMillis() - millis).toString());
        return syncInstance;
    }


    /**
     * Force clear the cache for given module due to insconsistent data.
     *
     * @param cacheTime Cache time selected by user.
     * @param moduleToSync Module to cache.
     * @param locale The locale to apply.
     *
     * @throws HaloNetException
     */
    public void forceCacheModule(int cacheTime, @NonNull String moduleToSync, @Nullable String locale) throws HaloNetException {
        HaloRequest request = createSyncRequest(cacheTime, null, moduleToSync, locale, true);
        request.execute();
    }

    /**
     * Helper method to fromCursor the execution request for the CMS.
     *
     * @param fromSync     Time where the execution will start.
     * @param moduleToSync The module that will be synced.
     * @param locale       The locale.
     * @return The request created.
     */
    private HaloRequest createSyncRequest(int cacheTime, @Nullable Date fromSync, @NonNull String moduleToSync, @Nullable String locale, @NonNull Boolean forceCleanCache) {
        HaloRequest.Builder builder = HaloRequest.builder(mClientApi)
                .method(HaloRequestMethod.POST)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, HaloContentNetwork.URL_SYNC_MODULE)
                .body(HaloBodyFactory.jsonObjectBody(createSyncJSONBody(moduleToSync, locale, fromSync)));
        if (fromSync == null) {
            builder.cacheHeader(cacheTime);
            if(forceCleanCache){
                builder.cacheControl(HaloRequest.NO_CACHE);
            }
        }
        return builder.build();
    }


    /**
     * Creates the json body to request the execution information.
     *
     * @param moduleToSync The module that will be used to execution.
     * @param locale       The locale.
     * @param fromSync     The timestamp with the last execution performed.
     * @return The json object generated.
     */
    @NonNull
    private JSONObject createSyncJSONBody(@NonNull String moduleToSync, @Nullable String locale, @Nullable Date fromSync) {
        JSONObject json = new JSONObject();
        try {
            json.put("moduleName", moduleToSync);
            if (locale != null) {
                json.put("locale", locale);
            }
            if (fromSync != null) {
                json.put("fromSync", fromSync.getTime());
                json.put("toSync", new Date().getTime());
            }
        } catch (JSONException e) {
            Halog.wtf(getClass(), "Never should happen");
        }
        return json;
    }
}
