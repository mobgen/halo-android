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
 * Define the field to sort.
 */
@Keep
public class SortField {

    private SortField() {
    }

    /**
     * Determines the search sort operation.
     */
    @Keep
    @StringDef({NAME, CREATE, UPDATE, PUBLISHED, REMOVED, ARCHIVED, DELETE, CREATEDBY, UPDATEDBY, DELETEDBY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOperator {
    }

    /**
     * Identifier for instance name
     */
    @Api(2.4)
    @Keep
    public static final String NAME = "name";
    /**
     * Identifier for create date
     */
    @Api(2.4)
    @Keep
    public static final String CREATE = "createdAt";
    /**
     * Identifier for update date.
     */
    @Api(2.4)
    @Keep
    public static final String UPDATE = "updatedAt";
    /**
     * Identifier for published date.
     */
    @Api(2.4)
    @Keep
    public static final String PUBLISHED = "publishedAt";
    /**
     * Identifier remove date.
     */
    @Api(2.4)
    public static final String REMOVED = "removedAt";

    /**
     * Identifier for archive date.
     */
    @Api(2.4)
    @Keep
    public static final String ARCHIVED = "archivedAt";

    /**
     * Identifier for delete date.
     */
    @Api(2.4)
    @Keep
    public static final String DELETE = "deleteAt";

    /**
     * Identifier for created by field.
     */
    @Api(2.4)
    @Keep
    public static final String CREATEDBY = "createdBy";

    /**
     * Identifier for updated by field.
     */
    @Api(2.4)
    @Keep
    public static final String UPDATEDBY = "updatedBy";

    /**
     * Identifier for deleted by field.
     */
    @Api(2.4)
    @Keep
    public static final String DELETEDBY = "deletedBy";

}
