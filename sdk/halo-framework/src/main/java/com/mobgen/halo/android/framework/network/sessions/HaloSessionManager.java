package com.mobgen.halo.android.framework.network.sessions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.HashMap;
import java.util.Map;

/**
 * It is a manager that can hold the session objects created by halo and other
 * application components.
 */
public class HaloSessionManager {

    /**
     * The session map with all the sessions.
     */
    private final Map<String, HaloSession> mSessionHolder;

    /**
     * Constructor for the session manager.
     */
    @Api(1.0)
    public HaloSessionManager() {
        mSessionHolder = new HashMap<>(1);
    }

    /**
     * Adds a session element with a given name. It is important to avoid overriding
     * a previous session if it is present.
     *
     * @param sessionName The session name.
     * @param session     The session object.
     */
    @Api(1.0)
    public void setSession(@NonNull String sessionName, @NonNull HaloSession session) {
        mSessionHolder.put(sessionName, session);
    }

    /**
     * Provides the session stored by a given name.
     *
     * @param session The session to take.
     * @return The session found or null if there is no session available.
     */
    @Api(1.0)
    @Nullable
    public HaloSession getSession(@NonNull String session) {
        return mSessionHolder.get(session);
    }

    /**
     * Flushes the session specified.
     *
     * @param sessionName The session name to flush.
     */
    @Api(1.0)
    public void flushSession(@NonNull String sessionName) {
        mSessionHolder.remove(sessionName);
    }
}
