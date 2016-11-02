package com.mobgen.halo.android.translations.callbacks;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * This class notifies if there is an error while doing the translations.
 */
@Keep
public interface TranslationsErrorListener {

    /**
     * The status of the translations load.
     *
     * @param status The status of the loading.
     */
    @Keep
    @Api(2.0)
    void onTranslationsError(@NonNull HaloStatus status);
}
