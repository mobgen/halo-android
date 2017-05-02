package com.mobgen.halo.android.content.models;

import android.support.annotation.Keep;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fernandosouto on 11/04/17.
 */

/**
 * Defines the batch operations.
 *
 */
@Keep
public class BatchOperator {

    private BatchOperator() {
    }

    /**
     * Determines the batch operation
     */
    @Keep
    @StringDef({TRUNCATE, CREATE, CREATEORUPDATE, UPDATE, DELETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BatchOperation {
    }

    /**
     * Identifier for truncate operation
     */
    @Api(2.3)
    public static final String TRUNCATE = "truncate";
    /**
     * Identifier for create operation
     */
    @Api(2.3)
    public static final String CREATE = "create";
    /**
     * Identifier for update operation
     */
    @Api(2.3)
    public static final String UPDATE = "update";
    /**
     * Identifier for create or update operation
     */
    @Api(2.3)
    public static final String CREATEORUPDATE = "createOrUpdate";
    /**
     * Identifier for delete operation
     */
    @Api(2.3)
    public static final String DELETE = "delete";
}
