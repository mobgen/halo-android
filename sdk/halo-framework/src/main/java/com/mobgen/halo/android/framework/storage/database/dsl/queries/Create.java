package com.mobgen.halo.android.framework.storage.database.dsl.queries;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.ReflectionUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Query to create items in a database.
 */
public class Create extends Query {

    /**
     * Constructor for the create statement.
     */
    private Create() {
        super();
        builder().append("CREATE ");
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
            builder().append("TABLE IF NOT EXISTS ");
            String tableName = ORMUtils.getTableName(table);
            boolean hasMultipleKeys = ORMUtils.hasMultipleKeys(table);
            builder().append(tableName);
            Field[] fields = ReflectionUtils.getFields(table, true);
            builder().append("(");
            boolean hasComma = false;
            List<Field> multipleKeyFields = hasMultipleKeys ? new ArrayList<Field>(fields.length) : null;
            for (Field field : fields) {
                Column column = ORMUtils.getColumnAnnotation(field);
                if (column != null) {
                    String fieldName = ORMUtils.getColumnName(field);
                    boolean isPrimaryKey = ORMUtils.isPrimaryKey(column);
                    appendField(column, fieldName, isPrimaryKey && !hasMultipleKeys, hasComma);
                    if (hasMultipleKeys && isPrimaryKey) {
                        multipleKeyFields.add(field);
                    }
                    hasComma = true;
                }
            }
            //Add mutikey possibility
            if (hasMultipleKeys && !multipleKeyFields.isEmpty()) {
                addMultiFields(multipleKeyFields);
            }
            builder().append(")");
        }

        private void addMultiFields(List<Field> multipleKeyFields) {
            builder().append(", PRIMARY KEY");
            builder().append("(");
            boolean hasComma = false;
            for (Field field : multipleKeyFields) {
                if (hasComma) {
                    builder().append(",");
                }
                builder().append(ORMUtils.getColumnName(field));
                hasComma = true;
            }
            builder().append(")");
        }

        /**
         * Analyzes a field and creates it.
         *
         * @param fieldName The field that will be created.
         */
        private void appendField(Column column, String fieldName, boolean isPrimaryKey, boolean hasComma) {
            if (hasComma) {
                builder().append(",");
            }
            builder().append(fieldName);
            builder().append(" ").append(ORMUtils.getColumnType(column));
            boolean isUnique = ORMUtils.isUnique(column);
            boolean hasReference = ORMUtils.hasReference(column);
            if (isPrimaryKey || isUnique) {
                if (isPrimaryKey) {
                    builder().append(" PRIMARY KEY");
                } else {
                    builder().append(" UNIQUE");
                }
            } else if (hasReference) {
                builder().append(" REFERENCES ").append(ORMUtils.getTableName(column.references()));
                builder().append("(").append(fieldName).append(")");
            }
        }

        /**
         * Delegates the database helper.
         *
         * @param databaseHelper The database helper.
         * @param descriptions   The query descriptions.
         */
        public void on(SQLiteDatabase databaseHelper, @Nullable String... descriptions) {
            Create.this.on(databaseHelper, descriptions);
        }
    }

    /**
     * The index syntax class
     */
    public class IndexSyntax {

        /**
         * Constructs an index on a given table.
         *
         * @param table     The table on which the index should be constructed.
         * @param indexName The index name.
         * @param columns   The columns.
         */
        private IndexSyntax(@NonNull Class<? extends HaloTable> table, @NonNull String indexName, @NonNull String[] columns) {
            builder().append("UNIQUE INDEX ");
            builder().append(indexName).append(" ");
            builder().append("ON ").append(ORMUtils.getTableName(table)).append(" ");
            builder().append("(");
            boolean hasComma = false;
            for (String column : columns) {
                if (hasComma) {
                    builder().append(",");
                }
                builder().append(column);
                hasComma = true;
            }
            builder().append(")");
        }

        /**
         * Delegates the database helper.
         *
         * @param databaseHelper The database helper.
         * @param descriptions   The query descriptions.
         */
        public void on(SQLiteDatabase databaseHelper, @Nullable String... descriptions) {
            Create.this.on(databaseHelper, descriptions);
        }

    }

    /**
     * Creates a table.
     *
     * @param table The table to create.
     * @return The table syntax expression.
     */
    public static TableSyntax table(@NonNull Class<? extends HaloTable> table) {
        return new Create().tableInner(table);
    }

    /**
     * Creates an index.
     *
     * @param table     The table where the index will be created.
     * @param indexName The index name.
     * @param columns   The columns that will be indexed.
     * @return The index syntax grammar.
     */
    public static IndexSyntax index(@NonNull Class<? extends HaloTable> table, @NonNull String indexName, @NonNull String[] columns) {
        return new Create().indexInner(table, indexName, columns);
    }

    /**
     * Creates a new table syntax.
     *
     * @param table The table to create.
     * @return The syntax.
     */
    private TableSyntax tableInner(@NonNull Class<? extends HaloTable> table) {
        return new TableSyntax(table);
    }

    /**
     * Creates a new index syntax.
     *
     * @param table     The table to act.
     * @param indexName The index name.
     * @param columns   The columns for the index.
     * @return The index syntax.
     */
    private IndexSyntax indexInner(@NonNull Class<? extends HaloTable> table, @NonNull String indexName, @NonNull String[] columns) {
        return new IndexSyntax(table, indexName, columns);
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
