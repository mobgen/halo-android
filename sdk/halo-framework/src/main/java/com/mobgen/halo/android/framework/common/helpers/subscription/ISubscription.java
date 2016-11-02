package com.mobgen.halo.android.framework.common.helpers.subscription;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Unsubscribe interface for notification events.
 */
public interface ISubscription {
    /**
     * Unsubscribes from the subscription mechanism.
     */
    @Api(1.3)
    void unsubscribe();
}
