package com.mobgen.halo.android.translations.callbacks;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.translations.HaloTranslationsApi;

/**
 * Allows the user to define a default text based on the key or return null to use the default
 * text provided in the configuration.
 */
@Keep
public interface DefaultTextHandler {

    /**
     * Provides the default text for a given key.
     *
     * @param key       The key requested to {@link HaloTranslationsApi#getText(String)}.
     * @param isLoading Tells if the texts are being loaded.
     * @return The text provided.
     */
    @Keep
    @Api(2.0)
    @Nullable
    String provideDefaultText(@Nullable String key, boolean isLoading);
}
