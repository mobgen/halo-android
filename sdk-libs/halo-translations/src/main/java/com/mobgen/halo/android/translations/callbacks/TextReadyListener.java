package com.mobgen.halo.android.translations.callbacks;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Callback to listen for localized texts that come from the CMS.
 */
@Keep
public interface TextReadyListener {

    /**
     * Callback for the localized text async request.
     *
     * @param key  The key.
     * @param text The text.
     */
    @Keep
    @Api(2.0)
    void onTextReady(@Nullable String key, @Nullable String text);
}
