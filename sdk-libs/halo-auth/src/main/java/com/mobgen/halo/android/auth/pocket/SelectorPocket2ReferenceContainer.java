package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;
import com.mobgen.halo.android.sdk.core.selectors.Selector;

import java.util.List;

/**
 * Created by f.souto.gonzalez on 22/06/2017.
 */

/**
 * @hide The instance parse selector brings a parsing method that allows
 * to move from a parsed instance to another parsed instance.
 */
public class SelectorPocket2ReferenceContainer extends Selector<List<ReferenceContainer>> {
    /**
     * The data provider
     */
    private PocketDataProvider<List<ReferenceContainer>> mDataProvider;

    /**
     * Parses from Pocket content to the a list of ReferenceContainer.
     */
    private ISelectorConverter<List<ReferenceContainer>, Pocket> mPocketReferenceConverter;

    /**
     * Creates a selector to parse between a parsed instance and another parsed
     * instance.
     */
    public SelectorPocket2ReferenceContainer(@NonNull PocketDataProvider dataProvider,
                                             @NonNull ISelectorConverter<List<ReferenceContainer>, Pocket> pocketReferenceConverter) {
        super(Data.NETWORK_ONLY);
        mDataProvider = dataProvider;
        mPocketReferenceConverter = pocketReferenceConverter;
    }

    @NonNull
    @Override
    public HaloResultV2<List<ReferenceContainer>> executeInteractor() throws Exception {
        HaloResultV2<List<ReferenceContainer>> result;
        result = mPocketReferenceConverter.convert(mDataProvider.fromNetwork());
        return result;
    }
}