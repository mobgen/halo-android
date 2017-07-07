package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * Created by f.souto.gonzalez on 21/06/2017.
 */

/**
 * Converter to convert a pocket data request to ReferenceContainer list.
 *
 * @hide The instance class converter to ReferenceContainer.
 */
public class Pocket2ReferenceContainerConverter implements ISelectorConverter<List<ReferenceContainer>, Pocket> {

    @NonNull
    @Override
    public HaloResultV2<List<ReferenceContainer>> convert(@NonNull HaloResultV2<Pocket> result) throws HaloParsingException, HaloNetException {
        Pocket pocket = result.data();
        List<ReferenceContainer> parsedData = null;
        if (pocket != null) {
            parsedData = pocket.getReferences();
        }
        return new HaloResultV2<>(result.status(), parsedData);
    }
}
