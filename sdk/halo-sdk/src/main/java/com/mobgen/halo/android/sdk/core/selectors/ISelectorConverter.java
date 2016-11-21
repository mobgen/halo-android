package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * The converter that converts from an unparsed item to a parsed one.
 */
@Keep
public interface ISelectorConverter<P, U> {
    /**
     * Converts from parsed to unparsed.
     *
     * @param item The result.
     * @return The result converted.
     * @throws Exception Exception produced during the parsing.
     */
    @NonNull
    @Api(2.0)
    HaloResultV2<P> convert(@NonNull HaloResultV2<U> item) throws Exception;
}