package com.mobgen.halo.android.framework.storage.database.dsl.queries;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.ReflectionUtils;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Drop request builder query
 */

public class Drop extends Query {


    /**
     * Private drop constructor.
     */
    private Drop() {
        super();
        builder().append("DROP ");
    }

    /**
     * The table syntax class.
     */
    public class TableSyntax {

        /**
         * The table constructor.
         *
         * @param table The table class.
         */
        private TableSyntax(@NonNull Class<? extends HaloTable> table) {
            builder().append("TABLE IF EXISTS ");
            String tableName = ORMUtils.getTableName(table);
            boolean hasMultipleKeys = ORMUtils.hasMultipleKeys(table);
            builder().append(tableName);
        }



        /**
         * Delegates the database helper.
         *
         * @param databaseHelper The database helper.
         * @param descriptions   The query descriptions.
         */
        public void on(SQLiteDatabase databaseHelper, @Nullable String... descriptions) {
            Drop.this.on(databaseHelper, descriptions);
        }
    }

    /**
     * Drops a table.
     *
     * @param table The table to drop.
     * @return The table syntax expression.
     */
    public static Drop.TableSyntax table(@NonNull Class<? extends HaloTable> table) {
        return new Drop().tableInner(table);
    }

    /**
     * Creates a new table syntax.
     *
     * @param table The table to create.
     * @return The syntax.
     */
    private Drop.TableSyntax tableInner(@NonNull Class<? extends HaloTable> table) {
        return new Drop.TableSyntax(table);
    }

    /**
     * The execution on the database context.
     *
     * @param database     The helper.
     * @param descriptions Descriptions for the query.
     */
    private void on(@NonNull SQLiteDatabase database, @Nullable String... descriptions) {
        database.execSQL(print(descriptions));
    }
}
