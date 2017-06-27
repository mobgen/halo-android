package com.mobgen.halo.android.content.models;

import android.support.annotation.Keep;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by f.souto.gonzalez on 27/06/2017.
 */

/**
 * Define the order type to apply.
 */
@Keep
public class SortOrder {

    private SortOrder() {
    }

    /**
     * Determines the search sort operation.
     */
    @Keep
    @StringDef({ASCENDING, DESCENDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortType {
    }

    /**
     * SortType ascending.
     */
    @Api(2.4)
    @Keep
    public static final String ASCENDING = "asc";

    /**
     * SortType descending
     */
    @Api(2.4)
    @Keep
    public static final String DESCENDING = "desc";

}
