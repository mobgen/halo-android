package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Interface that is intended to be used to collect tags. Every tag should implement this interface and be registered
 * using the {@link Halo.Installer#addTagCollector(TagCollector) addTagCollector} method.
 */
@Keep
public interface TagCollector {

    /**
     * Collects a tag from the current device. This method is executed always in a background thread so you can
     * make a request request to any server if the tag can not be retrieved immediately.
     *
     * @param context The application context.
     * @return The segmentation tag created for this tag collector or null if this collector does not onRegister any tag.
     */
    @Api(1.0)
    HaloSegmentationTag collect(Context context);
}
