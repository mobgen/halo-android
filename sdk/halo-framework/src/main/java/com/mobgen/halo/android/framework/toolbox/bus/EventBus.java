package com.mobgen.halo.android.framework.toolbox.bus;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The event hub interface that every event hub should implement.
 */
public interface EventBus {

    /**
     * Subscribes to a given event id.
     *
     * @param eventSubscriber The subscriber.
     * @param id              The id to subscribe.
     * @return The subscription created.
     */
    @Api(1.3)
    @NonNull
    Subscription subscribe(@NonNull Subscriber eventSubscriber, @NonNull EventId id);

    /**
     * Unsubscribes from the hub of events.
     *
     * @param eventSubscriber The event subscriber to remove.
     * @param eventId         The event id to unsubscribe.
     */
    @Api(1.3)
    void unsubscribe(@NonNull SubscriberAdapter eventSubscriber, @NonNull EventId eventId);

    /**
     * Emits an event in the hub.
     *
     * @param event The event to emit.
     */
    @Api(1.3)
    void emit(@NonNull Event event);
}
