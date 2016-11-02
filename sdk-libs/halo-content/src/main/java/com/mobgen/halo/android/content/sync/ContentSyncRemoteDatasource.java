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
import java.util.concurrent.TimeUnit;

/**
 * @hide Remote datasource that request synced instances.
 */
public class ContentSyncRemoteDatasource {

    /**
     * Default cache time for the server.
     */
    private static final long SERVER_CACHE_TIME = TimeUnit.DAYS.toSeconds(1);

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
    public HaloInstanceSync syncModule(@NonNull String moduleToSync, @Nullable String locale, @Nullable Date fromSync) throws HaloNetException {
        long millis = System.currentTimeMillis();
        HaloInstanceSync syncInstance = createSyncRequest(fromSync, moduleToSync, locale).execute(HaloInstanceSync.class);
        boolean isFirstSync = fromSync == null;
        if (isFirstSync) {
            HaloRequest request = createSyncRequest(syncInstance.getSyncDate(), moduleToSync, locale);
            HaloInstanceSync fromCacheSyncInstance = request.execute(HaloInstanceSync.class);
            syncInstance.mergeWith(fromCacheSyncInstance);
        }
        Halog.d(getClass(), Long.valueOf(System.currentTimeMillis() - millis).toString());
        return syncInstance;
    }

    /**
     * Helper method to fromCursor the execution request for the CMS.
     *
     * @param fromSync     Time where the execution will start.
     * @param moduleToSync The module that will be synced.
     * @param locale       The locale.
     * @return The request created.
     */
    private HaloRequest createSyncRequest(@Nullable Date fromSync, @NonNull String moduleToSync, @Nullable String locale) {
        HaloRequest.Builder builder = HaloRequest.builder(mClientApi)
                .method(HaloRequestMethod.POST)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, HaloContentNetwork.URL_SYNC_MODULE)
                .body(HaloBodyFactory.jsonObjectBody(createSyncJSONBody(moduleToSync, locale, fromSync)));
        if (fromSync == null) {
            builder.header("to-cache", String.valueOf(SERVER_CACHE_TIME));
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
