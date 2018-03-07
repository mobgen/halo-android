package com.mobgen.halo.android.notifications.events;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by f.souto.gonzalez on 19/01/2018.
 */

/**
 * Notification action events.
 */
@Keep
public class NotificationEventsActions {

    @Keep
    @StringDef({PUSH_RECEIPT, PUSH_OPEN, PUSH_DISMISS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {
    }

    /**
     * Action event when a push was receipt.
     */
    @Keep
    public static final String PUSH_RECEIPT = "receipt";

    @Keep
    public static final String PUSH_OPEN = "open";

    @Keep
    public static final String PUSH_DISMISS = "dismiss";
}