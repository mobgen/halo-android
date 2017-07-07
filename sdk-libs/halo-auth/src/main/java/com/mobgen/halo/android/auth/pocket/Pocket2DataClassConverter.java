package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

/**
 * Created by f.souto.gonzalez on 21/06/2017.
 */

/**
 * Converter to convert a pocket data request to custom class.
 *
 * @param <T> The class to cast the data from pocket.
 * @hide The instance class converter.
 */

public class Pocket2DataClassConverter<T> implements ISelectorConverter<T, Pocket> {

    /**
     * The class type.
     */
    private Class mClazz;

    /**
     * Constructor for the instance class converter.
     *
     * @param clazz The to change from and to.
     */
    public Pocket2DataClassConverter(@NonNull Class clazz) {
        mClazz = clazz;
    }

    @NonNull
    @Override
    public HaloResultV2<T> convert(@NonNull HaloResultV2<Pocket> result) throws HaloParsingException, HaloNetException {
        Pocket pocket = result.data();
        T parsedData = null;
        if (pocket != null) {
            if (pocket.getClass().equals(mClazz)) {
                parsedData = (T) pocket;
            } else {
                parsedData = pocket.getValues(mClazz);
            }
        }
        return new HaloResultV2<>(result.status(), parsedData);
    }
}
