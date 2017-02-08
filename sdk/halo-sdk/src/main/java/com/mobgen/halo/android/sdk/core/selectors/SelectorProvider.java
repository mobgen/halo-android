package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * Provider interface that gives the information needed.
 */
@Keep
public interface SelectorProvider<P, U> {
    /**
     * Called when it is a {@link Data.Policy#NETWORK_ONLY}.
     *
     * @return The result obtained.
     * @throws HaloNetException The network error produced.
     */
    @NonNull
    @Api(2.0)
    HaloResultV2<P> fromNetwork() throws HaloNetException, HaloParsingException;

    /**
     * Called when it is as a {@link Data.Policy#STORAGE_ONLY}.
     *
     * @return The data obtained from the dataLocal storage.
     * @throws HaloStorageException The storage error produced.
     */
    @NonNull
    @Api(2.0)
    HaloResultV2<U> fromStorage() throws HaloStorageException;

    /**
     * Called when it is as a {@link Data.Policy#NETWORK_AND_STORAGE}.
     *
     * @return Provides the information from network or cache.
     * @throws HaloNetException     The network exception produced.
     * @throws HaloStorageException The storage error produced.
     */
    @NonNull
    @Api(2.0)
    HaloResultV2<U> fromNetworkStorage() throws HaloNetException, HaloStorageException;
}