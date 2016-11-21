package com.mobgen.halo.android.framework.toolbox.bus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * This is a broadcast receiver with the possibility to be notified in case
 * it is needed.
 */
public class SubscriberAdapter extends BroadcastReceiver {

    /**
     * The subscriber adapted.
     */
    private Subscriber mSubscriber;

    /**
     * The event id.
     */
    private EventId mEventId;

    /**
     * Constructor for the broadcast subscriber/
     *
     * @param subscriber The subscriber adapted.
     * @param eventId The event id for the subscription.
     */
    public SubscriberAdapter(@NonNull Subscriber subscriber, @NonNull EventId eventId) {
        AssertionUtils.notNull(subscriber, "subscriber");
        AssertionUtils.notNull(eventId, "eventId");
        mSubscriber = subscriber;
        mEventId = eventId;
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        mSubscriber.onEventReceived(new Event(mEventId, intent.getExtras()));
    }
}
