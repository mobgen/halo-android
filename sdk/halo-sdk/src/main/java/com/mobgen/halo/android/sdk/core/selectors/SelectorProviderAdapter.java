package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * Adapter to help implementing different providers without adding boilerplate.
 */
@Keep
public abstract class SelectorProviderAdapter<P, U> implements SelectorProvider<P, U> {

    @NonNull
    @Override
    public HaloResultV2<P> fromNetwork() throws HaloNetException {
        throw new UnsupportedOperationException("This network operation has not been implemented. " + getClass().getCanonicalName());
    }

    @NonNull
    @Override
    public HaloResultV2<U> fromStorage() throws HaloStorageException {
        throw new UnsupportedOperationException("This storage operation has not been implemented. " + getClass().getCanonicalName());
    }

    @NonNull
    @Override
    public HaloResultV2<U> fromNetworkStorage() throws HaloNetException, HaloStorageException {
        throw new UnsupportedOperationException("This network and storage operation has not been implemented. " + getClass().getCanonicalName());
    }
}
