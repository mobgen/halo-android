package com.mobgen.halo.android.notifications.events;


import android.support.annotation.NonNull;
import android.util.Log;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
/**
 * Created by f.souto.gonzalez on 19/01/2018.
 */

/**
 * Notification action event remote source
 */
public class NotificationEventRemoteDatasource {

    /**
     * The url to send action data.
     */
    public static final String URL_NOTIFICATION_EVENT = "api/push/schedule/activity";

    /**
     * The client api.
     */
    private HaloNetworkApi mClientApi;

    /**
     * Constructor datas ource to send data action with push notifications
     *
     * @param clientApi The client api.
     */
    public NotificationEventRemoteDatasource(@NonNull HaloNetworkApi clientApi) {
        mClientApi = clientApi;
    }

    /**
     * Notify the action event action received.
     *
     * @param haloPushEvent
     * @return The HALO push event.
     * @throws HaloNetException
     */
    public HaloPushEvent notifyEvent(HaloPushEvent haloPushEvent) throws HaloNetException {
        return HaloRequest.builder(mClientApi)
                .url(HaloNetworkConstants.HALO_ENDPOINT_ID, URL_NOTIFICATION_EVENT)
                .method(HaloRequestMethod.POST)
                .body(HaloBodyFactory.formBody()
                        .add("device", haloPushEvent.getDevice())
                        .add("schedule", haloPushEvent.getSchedule())
                        .add("action", haloPushEvent.getAction())
                        .build())
                .build().execute(HaloPushEvent.class);
    }
}
