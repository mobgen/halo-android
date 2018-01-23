package com.mobgen.halo.android.notifications.events;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
/**
 * Created by f.souto.gonzalez on 19/01/2018.
 */

/**
 * The event notification repository to provide.
 */
public class NotificationEventRepository {

    /**
     * The event notification data source.
     */
    private NotificationEventRemoteDatasource mRemoteDatasource;

    /**
     * Constructor for the repository.
     *
     * @param notificationEventRemoteDatasource The remote data source.
     */
    public NotificationEventRepository(@NonNull NotificationEventRemoteDatasource notificationEventRemoteDatasource) {
        AssertionUtils.notNull(notificationEventRemoteDatasource, "remoteDatasource");
        mRemoteDatasource = notificationEventRemoteDatasource;

    }

    /**
     * Notify the action event.
     *
     * @param haloPushEvent The HALO push action event.
     * @return
     */
    public HaloResultV2<HaloPushEvent> notifyEvent(HaloPushEvent haloPushEvent) {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloPushEvent haloPushEventResponse = null;
        try {
            haloPushEventResponse = mRemoteDatasource.notifyEvent(haloPushEvent);
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), haloPushEventResponse);

    }

}
