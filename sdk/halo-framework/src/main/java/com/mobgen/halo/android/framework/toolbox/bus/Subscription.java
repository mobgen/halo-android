package com.mobgen.halo.android.framework.toolbox.bus;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * Subscription for the event.
 */
public class Subscription implements ISubscription {

    /**
     * The event hub in which this subscription is created.
     */
    private EventBus mEventHub;
    /**
     * The subscriber.
     */
    private SubscriberAdapter mSubscriber;
    /**
     * The event id.
     */
    private EventId mEventId;

    /**
     * Creates a subscription for the event id and the subscriber.
     *
     * @param eventHub   The event hub.
     * @param subscriber The subscriber.
     * @param eventId    The subscription.
     */
    @Api(1.3)
    public Subscription(@NonNull EventBus eventHub, @NonNull SubscriberAdapter subscriber, @NonNull EventId eventId) {
        AssertionUtils.notNull(eventHub, "eventHub");
        AssertionUtils.notNull(subscriber, "subscriber");
        AssertionUtils.notNull(eventId, "eventId");
        mEventHub = eventHub;
        mSubscriber = subscriber;
        mEventId = eventId;
    }

    /**
     * Unsubscribes from the eventhub.
     */
    @Api(1.3)
    @Override
    public void unsubscribe() {
        mEventHub.unsubscribe(mSubscriber, mEventId);
    }
}