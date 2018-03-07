package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.content.Intent;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;
import com.mobgen.halo.android.notifications.services.NotificationEmitter;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Created by f.souto.gonzalez on 23/01/2018.
 */

public class NotificationActionIntentInstruments {

    public static Intent givenAEventActionIntent(@NotificationEventsActions.EventType String eventType, String scheduleID) {
        return  new Intent().setAction(HaloUtils.getEventName(Halo.instance().context(), NotificationEmitter.NOTIFICATION_EVENT) + eventType)
                .putExtra("action",eventType)
                .putExtra("scheduleId", scheduleID);
    }
}
