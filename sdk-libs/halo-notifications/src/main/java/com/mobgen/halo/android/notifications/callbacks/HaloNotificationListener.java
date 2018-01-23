package com.mobgen.halo.android.notifications.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
/**
 * Created by f.souto.gonzalez on 23/01/2018.
 */
/**
 * Provides a callback to make some operation over every notification item.
 */
@Keep
public interface HaloNotificationListener {

    /**
     * Callback to perform some operation when a notification arrives tho the system.
     *
     * @param context The context.
     * @param from    The source of this notification.
     * @param data    The data received to be displayed in the notification.
     * @param extra   Extra values added to display custom information
     */
    @Keep
    @Api(2.0)
    void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra);
}
