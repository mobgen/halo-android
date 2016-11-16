package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.Context;
import android.content.Intent;

/**
 * The receiver interface.
 */
interface Receiver {
    /**
     * Receives an intent to satisfy a trigger.
     *
     * @param context The context.
     * @param intent  The intent.
     */
    void onReceive(Context context, Intent intent);

    /**
     * Provides the identifier.
     *
     * @return The identifier.
     */
    String getIdentify();
}
