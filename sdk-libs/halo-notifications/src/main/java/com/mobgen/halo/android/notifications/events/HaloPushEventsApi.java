package com.mobgen.halo.android.notifications.events;


import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Created by f.souto.gonzalez on 12/02/2018.
 */

/**
 * The halo push event api to notify event action on push notifications
 */
public class HaloPushEventsApi extends HaloPluginApi {


    /**
     * Constructor for the push notifications API.
     *
     * @param halo The halo instance.
     */
    private HaloPushEventsApi(@NonNull final Halo halo) {
        super(halo);

    }

    /**
     * Creates the push api with the halo instance. You need the
     * halo instance to be properly configured and ready to be used.
     *
     * @param halo The halo instance.
     * @return The created notifications API.
     */
    @Keep
    @NonNull
    protected static HaloPushEventsApi with(@NonNull Halo halo) {
        return new HaloPushEventsApi(halo);
    }


    /**
     * Notify a action event on the push notification.
     *
     * @param haloPushEvent The HALO push notification action event.
     * @return HaloInteractorExecutor<HaloPushEvent> The interactor executor
     */
    @Keep
    @NonNull
    protected HaloInteractorExecutor<HaloPushEvent> notifyPushEvent(@NonNull HaloPushEvent haloPushEvent) {
        return new HaloInteractorExecutor<>(halo(),
                "Push notify event action",
                new NotificationEventInteractor(new NotificationEventRepository(new NotificationEventRemoteDatasource(halo().framework().network())), haloPushEvent)
        );
    }

}
