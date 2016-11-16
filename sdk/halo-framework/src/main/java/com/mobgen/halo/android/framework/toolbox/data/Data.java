package com.mobgen.halo.android.framework.toolbox.data;

import android.support.annotation.IntDef;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Expresses the data mode to access the data.
 */
public final class Data {

    /**
     * Private constructor for the data.
     */
    private Data() {
    }

    /**
     * Synchronization execution mode annotation to allow static analysis on the sdk. It allows us
     * to make the sdk more easy to match with the client implementation.
     */
    @IntDef({NETWORK_AND_STORAGE, NETWORK_ONLY, STORAGE_ONLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Policy {
    }

    /**
     * Tries to get the data updated from the network and store it in the dataLocal storage. Once done
     * it can parse the data and retrieve this information.
     */
    @Api(1.3)
    public static final int NETWORK_AND_STORAGE = 0;
    /**
     * The synchronization engine is not triggered and you will receive the data directly from
     * the network without handling any database synchronization.
     */
    @Api(1.3)
    public static final int NETWORK_ONLY = 1;
    /**
     * Makes the request to bring the data only from the dataLocal database without triggering any request
     * process.
     */
    @Api(1.3)
    public static final int STORAGE_ONLY = 2;
}
