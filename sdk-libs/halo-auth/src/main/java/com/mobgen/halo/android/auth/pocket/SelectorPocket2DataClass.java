package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;
import com.mobgen.halo.android.sdk.core.selectors.Selector;

/**
 * Created by f.souto.gonzalez on 22/06/2017.
 */

/**
 * @hide The instance parse selector brings a parsing method that allows
 * to move from a parsed instance to another parsed instance.
 */
public class SelectorPocket2DataClass<T> extends Selector<T> {
    /**
     * The data provider.
     */
    private PocketDataProvider<T> mDataProvider;
    /**
     * Parses from Pocket content to the custom class.
     */
    private ISelectorConverter<T, Pocket> mPocketConverter;

    /**
     * Creates a selector to parse between a parsed pocket and another parsed
     * instance.
     */
    public SelectorPocket2DataClass(@NonNull PocketDataProvider dataProvider,
                                    @NonNull ISelectorConverter<T, Pocket> pocketConverter) {
        super(Data.NETWORK_ONLY);
        mDataProvider = dataProvider;
        mPocketConverter = pocketConverter;
    }

    @NonNull
    @Override
    public HaloResultV2<T> executeInteractor() throws Exception {
        HaloResultV2<T> result;
        result = mPocketConverter.convert(mDataProvider.fromNetwork());
        return result;
    }

    /**
     * Factory that creates the converters based on data types.
     */
    public interface Factory {
        /**
         * Creates a new selector with all the data needed for it.
         *
         * @param clazz The class.
         * @return The selector to transform to a custom class.
         */
        @NonNull
        <T> SelectorPocket2DataClass<T> createResultData(@NonNull PocketDataProvider dataProvider, @NonNull Class<T> clazz);

        /**
         * Creates a new selector with all the data needed for it.
         *
         * @return The selector to transform to a custom class.
         */
        @NonNull
        SelectorPocket2ReferenceContainer createResultReference(@NonNull PocketDataProvider dataProvider);
    }
}