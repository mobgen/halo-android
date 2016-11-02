package com.mobgen.halo.android.content.spec;

import android.support.annotation.Keep;
import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Network constants that contains the urls of HALO.
 */
@Keep
public class HaloContentNetwork {

    //GENERAL CONTENT
    /**
     * Performs search operations over the general content instances.
     */
    @Api(2.0)
    public static final String URL_SEARCH_INSTANCES = "api/generalcontent/instance/search";

    /**
     * Synchronization url for the general content instances.
     */
    @Api(2.0)
    public static final String URL_SYNC_MODULE = "api/generalcontent/instance/sync";
}
