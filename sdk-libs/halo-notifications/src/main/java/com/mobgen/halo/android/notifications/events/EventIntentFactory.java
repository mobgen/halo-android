package com.mobgen.halo.android.notifications.events;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.notifications.services.NotificationEmitter;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Created by f.souto.gonzalez on 12/02/2018.
 */

public class EventIntentFactory {
    /**
     * The action bundle name.
     */
    @NonNull
    private static String EVENT_ACTION = "action";
    /**
     * The scheduleId bundle name
     */
    @NonNull
    private static String SCHEDULE_ID = "scheduleId";

    private EventIntentFactory() {
    }

    /**
     * Create a intent for push notification event.
     *
     * @param action     The action event of the push.
     * @param scheduleId The scheduleId of the push.
     * @return The intent.
     */
    @NonNull
    public static Intent pushEventIntent(@NotificationEventsActions.EventType String action, @NonNull String scheduleId) {
        Intent pushEventIntent = new Intent();
        pushEventIntent.setAction(HaloUtils.getEventName(Halo.instance().context(), NotificationEmitter.NOTIFICATION_EVENT) + action);
        pushEventIntent.putExtra(EVENT_ACTION, action);
        pushEventIntent.putExtra(SCHEDULE_ID, scheduleId);

        return pushEventIntent;
    }
}
