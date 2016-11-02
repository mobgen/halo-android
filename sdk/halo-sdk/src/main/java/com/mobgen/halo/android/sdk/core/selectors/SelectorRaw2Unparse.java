package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * Selects the data from a dataLocal source and provides the result.
 */
@Keep
public class SelectorRaw2Unparse<U> extends Selector<U> {

    /**
     * The data provider that will be used to bring the content.
     */
    private SelectorProvider<?, U> mProvider;

    /**
     * The constructor for the selector.
     *
     * @param dataProvider The data provider.
     * @param mode         The mode used to execute this selector.
     */
    public SelectorRaw2Unparse(@NonNull SelectorProvider<?, U> dataProvider, @Data.Policy int mode) {
        super(mode);
        AssertionUtils.notNull(dataProvider, "dataProvider");
        mProvider = dataProvider;
    }

    @NonNull
    @Override
    public HaloResultV2<U> executeInteractor() throws HaloNetException, HaloStorageException {
        HaloResultV2<U> result;
        switch (dataPolicy()) {
            case Data.NETWORK_AND_STORAGE:
                result = mProvider.fromNetworkStorage();
                break;
            case Data.STORAGE_ONLY:
                result = mProvider.fromStorage();
                break;
            case Data.NETWORK_ONLY:
            default:
                throw new UnsupportedOperationException("The operation provided is not supported.");

        }
        return result;
    }
}