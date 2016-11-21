package com.mobgen.halo.android.framework.toolbox.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The base result data class for data obtained in the request process or from the database.
 */
public class HaloResultV2<P> {

    /**
     * The status of the data.
     */
    private final HaloStatus mStatus;

    /**
     * The data parsed.
     */
    private final P mData;

    /**
     * Create a new halo result based on the result from another item.
     *
     * @param wrappingResult The wrapping result.
     */
    @Api(2.0)
    public HaloResultV2(@NonNull HaloResultV2<P> wrappingResult) {
        this(wrappingResult.status(), wrappingResult.data());
    }

    /**
     * Constructor to create the wrapper for the result parsed and its status.
     *
     * @param status The status of this data.
     * @param data   The data stored.
     */
    @Api(2.0)
    public HaloResultV2(@NonNull HaloStatus status, @Nullable P data) {
        mStatus = status;
        mData = data;
    }

    /**
     * Provides the data from this request request.
     *
     * @return The data of this request.
     */
    @Api(2.0)
    @Nullable
    public P data() {
        return mData;
    }

    /**
     * Provides the status of the data.
     *
     * @return The status of the data.
     */
    @Api(2.0)
    @NonNull
    public HaloStatus status() {
        return mStatus;
    }
}
