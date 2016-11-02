package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * Selects a parsed instance from an unparsed source.
 */
@Keep
public class SelectorUnparse2Parse<P, U> extends Selector<P> {

    /**
     * The search query that can be performed.
     */
    private SelectorProvider<P, U> mDataProvider;
    /**
     * Converter between parsed or unparsed data.
     */
    private ISelectorConverter<P, U> mConverter;

    /**
     * Constructor for the instance selector.
     *
     * @param dataProvider The data provider.
     * @param converter    The converter to convert the instance to another one.
     * @param mode         The mode of the selector.
     */
    public SelectorUnparse2Parse(@NonNull SelectorProvider<P, U> dataProvider, @NonNull ISelectorConverter<P, U> converter, @Data.Policy int mode) {
        super(mode);
        AssertionUtils.notNull(dataProvider, "dataProvider");
        if(mode != Data.NETWORK_ONLY) {
            AssertionUtils.notNull(converter, "converter");
        }
        mDataProvider = dataProvider;
        mConverter = converter;
    }

    @NonNull
    @Override
    public HaloResultV2<P> executeInteractor() throws Exception {
        HaloResultV2<P> result;
        switch (dataPolicy()) {
            case Data.NETWORK_AND_STORAGE:
                result = mConverter.convert(mDataProvider.fromNetworkStorage());
                break;
            case Data.STORAGE_ONLY:
                result = mConverter.convert(mDataProvider.fromStorage());
                break;
            case Data.NETWORK_ONLY:
                result = mDataProvider.fromNetwork();
                break;
            default:
                throw new UnsupportedOperationException("The operation provided is not supported.");
        }
        return result;
    }
}