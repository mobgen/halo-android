package com.mobgen.halo.android.notifications.events;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
/**
 * Created by f.souto.gonzalez on 19/01/2018.
 */

/**
 * Send event action information after with a push notification
 */
public class NotificationEventInteractor implements HaloInteractorExecutor.Interactor<HaloPushEvent> {

    /**
     * Notification event repository.
     */
    private NotificationEventRepository mNotificationEventRepository;
    /**
     * Halo push event with the action information.
     */
    private HaloPushEvent mHaloPushEvent;

    /**
     * Constructor for the event notification interactor.
     *
     * @param notificationEventRepository The repository.
     * @param haloPushEvent The HALO push event action information.
     */
    public NotificationEventInteractor(@NonNull NotificationEventRepository notificationEventRepository, @NonNull HaloPushEvent haloPushEvent) {
        AssertionUtils.notNull(notificationEventRepository, "notificationEventRepository");
        AssertionUtils.notNull(haloPushEvent, "haloPushEvent");
        mNotificationEventRepository = notificationEventRepository;
        mHaloPushEvent = haloPushEvent;
    }


    @NonNull
    @Override
    public HaloResultV2<HaloPushEvent> executeInteractor() throws Exception {
        return mNotificationEventRepository.notifyEvent(mHaloPushEvent);
    }
}
