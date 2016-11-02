package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * ErrorStatus controller that controls the state of the device.
 */
interface StatusController {
    /**
     * Callback for the moment the status is created.
     *
     * @param context The context.
     */
    void onCreate(@NonNull Context context);

    /**
     * Callback produced when it is destroyed.
     */
    void onDestroy();
}
