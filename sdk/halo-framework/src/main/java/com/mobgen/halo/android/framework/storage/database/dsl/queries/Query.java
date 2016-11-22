package com.mobgen.halo.android.framework.storage.database.dsl.queries;


import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

/**
 * Abstract class to generate a query with the DSL.
 */
public abstract class Query {

    /**
     * The builder item for the query.
     */
    private final StringBuilder mBuilder;

    /**
     * The query that will be done.
     */
    public Query() {
        mBuilder = new StringBuilder();
    }

    /**
     * The builder.
     *
     * @return The builder.
     */
    public StringBuilder builder() {
        return mBuilder;
    }

    /**
     * Prints the query.
     *
     * @param descriptions Supports descriptions for the query.
     * @return Prints the query.
     */
    public String print(@Nullable String... descriptions) {
        String query = mBuilder.toString().trim();
        query += ";";
        Halog.d(getClass(), "-------------------------");
        if (descriptions != null) {
            for (String description : descriptions) {
                Halog.d(getClass(), description);
            }
        }
        Halog.d(getClass(), query);
        return query;
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }
}
