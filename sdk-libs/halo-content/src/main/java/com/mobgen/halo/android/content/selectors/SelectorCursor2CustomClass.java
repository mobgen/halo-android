package com.mobgen.halo.android.content.selectors;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;
import com.mobgen.halo.android.sdk.core.selectors.Selector;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProvider;

import java.util.List;

/**
 * @hide The instance parse selector brings a parsing method that allows
 * to move from a parsed instance to another parsed instance.
 */
public class SelectorCursor2CustomClass<F, P, U> extends Selector<F> {
    /**
     * The search query that can be performed.
     */
    private SelectorProvider<P, U> mDataProvider;
    /**
     * Parses from unparsed content to the custom class.
     */
    private ISelectorConverter<F, U> mUnparsedConverter;
    /**
     * Parses from parsed content to custom class.
     */
    private ISelectorConverter<F, P> mParsedConverter;

    /**
     * Creates a selector to parse between a parsed instance and another parsed
     * instance.
     *
     * @param dataProvider      The data provider.
     * @param unparsedConverter The unparsed converter.
     * @param parsedConverter   The parsed converter.
     * @param mode              The mode to select the data.
     */
    public SelectorCursor2CustomClass(@NonNull SelectorProvider<P, U> dataProvider,
                                      @NonNull ISelectorConverter<F, U> unparsedConverter,
                                      @NonNull ISelectorConverter<F, P> parsedConverter,
                                      @Data.Policy int mode) {
        super(mode);
        mDataProvider = dataProvider;
        mUnparsedConverter = unparsedConverter;
        mParsedConverter = parsedConverter;
    }

    @NonNull
    @Override
    public HaloResultV2<F> executeInteractor() throws Exception {
        HaloResultV2<F> result;
        switch (dataPolicy()) {
            case Data.NETWORK_AND_STORAGE:
                result = mUnparsedConverter.convert(mDataProvider.fromNetworkStorage());
                break;
            case Data.STORAGE_ONLY:
                result = mUnparsedConverter.convert(mDataProvider.fromStorage());
                break;
            case Data.NETWORK_ONLY:
                result = mParsedConverter.convert(mDataProvider.fromNetwork());
                break;
            default:
                throw new UnsupportedOperationException("The operation provided is not supported.");
        }
        return result;
    }

    /**
     * Factory that creates the converters based on data types.
     */
    public interface Factory<P, U> {
        /**
         * Creates a new selector with all the data needed for it.
         *
         * @param dataProvider The data provider.
         * @param clazz        The class.
         * @return The selector to transform to a custom class.
         */
        @NonNull
        <T> SelectorCursor2CustomClass<List<T>, P, U> createList(@NonNull SelectorProvider<P, U> dataProvider, @NonNull Class<T> clazz, @Data.Policy int mode);
    }
}