package com.mobgen.halo.android.framework.toolbox.bus;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The event id for a given event.
 */
public class EventId {

    /**
     * The id of the event.
     */
    private String mEventId;

    /**
     * Consrtuctor for the event id.
     *
     * @param id The id.
     */
    private EventId(@NonNull String id) {
        mEventId = id;
    }

    /**
     * Creates a new event.
     *
     * @param id The id of the event.
     * @return The event id object created,.
     */
    @Api(1.3)
    @NonNull
    public static EventId create(@NonNull String id) {
        return new EventId(id);
    }

    /**
     * Provides the id of this event.
     *
     * @return The id as a string of the event.
     */
    @Api(1.3)
    public String getId() {
        return mEventId;
    }
}