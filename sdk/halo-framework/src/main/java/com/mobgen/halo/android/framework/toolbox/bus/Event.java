package com.mobgen.halo.android.framework.toolbox.bus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * The event that will be emitted.
 */
public class Event {
    /**
     * The data emitted in the event.
     */
    @Nullable
    private Bundle mData;
    /**
     * The event id.
     */
    @NonNull
    private EventId mEventId;

    /**
     * Creates a new event.
     *
     * @param eventId The event id.
     */
    @Api(1.3)
    public Event(@NonNull EventId eventId) {
        this(eventId, null);
    }

    /**
     * Constructor with data.
     *
     * @param eventId The event id for this event.
     * @param data    The data provided.
     */
    @Api(1.3)
    public Event(@NonNull EventId eventId, @Nullable Bundle data) {
        AssertionUtils.notNull(eventId, "eventId");
        mEventId = eventId;
        mData = data;
    }

    /**
     * Provides the data inside the event.
     *
     * @return The data inside the event.
     */
    @Api(1.3)
    @Nullable
    public Bundle getData() {
        return mData;
    }

    /**
     * Get the event id.
     *
     * @return The event id.
     */
    @Api(1.3)
    @NonNull
    public EventId getEventId() {
        return mEventId;
    }

    @Override
    public String toString() {
        return "Event " + mEventId.getId() + " data: " + mData;
    }
}