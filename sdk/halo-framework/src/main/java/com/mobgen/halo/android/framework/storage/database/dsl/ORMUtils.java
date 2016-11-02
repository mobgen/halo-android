package com.mobgen.halo.android.framework.storage.database.dsl;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.utils.ReflectionUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Table;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Utility class for the ORM to retrieve data from fields with reflection.
 */
public final class ORMUtils {

    /**
     * Private constructor to avoid instances.
     */
    private ORMUtils() {
        //Private constructor to avoid instances
    }

    /**
     * The table that should be analyzed.
     *
     * @param table The table.
     * @return The string name of the table.
     */
    @Nullable
    public static String getTableName(@NonNull Class<? extends HaloTable> table) {
        String tableName = null;
        if (table.isAnnotationPresent(Table.class)) {
            tableName = table.getAnnotation(Table.class).value();
        }
        return tableName;
    }

    /**
     * Checks if the passed table has multiple keys.
     *
     * @param table The table.
     * @return True if it contains multiple keys. False otherwise.
     */
    public static boolean hasMultipleKeys(@NonNull Class<? extends HaloTable> table) {
        boolean multipleKeys = false;
        if (table.isAnnotationPresent(Table.class)) {
            multipleKeys = table.getAnnotation(Table.class).multipleKeys();
        }
        return multipleKeys;
    }

    /**
     * Checks if a field is nullable.
     *
     * @param column The column to check.
     * @return True if it is nullable, false otherwise.
     */
    public static boolean isUnique(@Nullable Column column) {
        boolean isUnique = false;
        if (column != null) {
            isUnique = column.unique();
        }
        return isUnique;
    }

    /**
     * Checks if a table has references.
     *
     * @param column The column.
     * @return True if it has references, false otherwise.
     */
    public static boolean hasReference(@Nullable Column column) {
        boolean hasReferences = false;
        if (column != null) {
            hasReferences = column.references() != Column.DEFAULT.class && !"".equals(column.columnReference());
        }
        return hasReferences;
    }

    /**
     * Determines if the column is a primary key.
     *
     * @param column The column.
     * @return True if this is a primary key. False otherwise.
     */
    public static boolean isPrimaryKey(@Nullable Column column) {
        boolean isPrimaryKey = false;
        if (column != null) {
            isPrimaryKey = column.isPrimaryKey();
        }
        return isPrimaryKey;
    }

    /**
     * Provides the column name.
     *
     * @param column The column name.
     * @return The name.
     */
    @Nullable
    public static String getColumnName(Field column) {
        return ReflectionUtils.valueOf(column, null, String.class);
    }

    /**
     * Provides the column annotation.
     *
     * @param field The field.
     * @return The column annotation.
     */
    @Nullable
    public static Column getColumnAnnotation(@NonNull Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class);
        }
        return null;
    }


    /**
     * Provides the name of the column type.
     *
     * @param column The column annotation.
     * @return The type name for sqlite.
     */
    @NonNull
    public static String getColumnType(@NonNull Column column) {
        Column.Type type = column.type();
        String typeString;
        switch (type) {
            case TEXT:
                typeString = "TEXT";
                break;
            case BLOB:
                typeString = "BLOB";
                break;
            case INTEGER:
                typeString = "INTEGER";
                break;
            case REAL:
                typeString = "REAL";
                break;
            case NUMERIC:
            case DATE:
            case BOOLEAN:
                typeString = "NUMERIC";
                break;
            default:
                throw new IllegalArgumentException("Only Column types are supported.");
        }
        return typeString;
    }

    /**
     * Binds the string or null value to an statement.
     *
     * @param statement The statement.
     * @param index     The index to bind.
     * @param value     The value to bind.
     */
    public static void bindStringOrNull(SQLiteStatement statement, int index, @Nullable String value) {
        if (value != null) {
            statement.bindString(index, value);
        } else {
            statement.bindNull(index);
        }
    }

    /**
     * Binds the date or null to the statement on the given index.
     *
     * @param statement The statement.
     * @param index     The index where the value will be bound.
     * @param value     The value to bind.
     */
    public static void bindDateOrNull(SQLiteStatement statement, int index, @Nullable Date value) {
        if (value != null) {
            statement.bindLong(index, value.getTime());
        } else {
            statement.bindNull(index);
        }
    }
}
