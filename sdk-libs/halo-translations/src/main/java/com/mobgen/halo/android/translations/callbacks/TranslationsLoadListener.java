package com.mobgen.halo.android.translations.callbacks;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Listener to receive a notification when the translations has been loaded.
 */
@Keep
public interface TranslationsLoadListener {

    /**
     * When the translations has been loaded this callback is called.
     */
    @Keep
    @Api(2.0)
    void onTranslationsLoaded();
}
