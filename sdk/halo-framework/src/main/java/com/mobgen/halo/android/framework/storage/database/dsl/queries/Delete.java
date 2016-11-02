package com.mobgen.halo.android.framework.storage.database.dsl.queries;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;

import java.util.Date;

/**
 * Delete request builder query.
 */
public class Delete extends Query {

    /**
     * Private delete constructor.
     */
    private Delete() {
        super();
        builder().append("DELETE ");
    }

    /**
     * From syntax for the delete query.
     *
     * @param table The table to take.
     * @return The from syntax instance.
     */
    public static FromSyntax from(@NonNull Class<? extends HaloTable> table) {
        return new Delete().columns(table);
    }

    /**
     * The columns to select.
     *
     * @param table The table with this columns.
     * @return The syntax for the from.
     */
    private FromSyntax columns(@NonNull Class<? extends HaloTable> table) {
        return new FromSyntax(table);
    }

    /**
     * Abstract inheritable executable sentence.
     */
    private abstract class ExecutableExpression {
        /**
         * Executes the sql in a database.
         *
         * @param database     The database.
         * @param descriptions Query descriptions.
         */
        public void on(@NonNull SQLiteDatabase database, @Nullable String... descriptions) {
            Delete.this.on(database, descriptions);
        }

        /**
         * Executes the sql in the helper by getting the database.
         *
         * @param storage      The database.
         * @param descriptions Descriptions.
         */
        public void on(@NonNull HaloDataLite storage, @Nullable String... descriptions) {
            on(storage.getDatabase(), descriptions);
        }
    }

    /**
     * The from syntax item.
     */
    public class FromSyntax extends ExecutableExpression {
        /**
         * Constructor for the table query.
         *
         * @param table The table to query.
         */
        private FromSyntax(@NonNull Class<? extends HaloTable> table) {
            builder().append("FROM ");
            builder().append(ORMUtils.getTableName(table)).append(" ");
        }


        /**
         * A where clause for the given field.
         *
         * @param column The param related.
         * @return The where syntax.
         */
        public WhereSyntax where(@Nullable String column) {
            return new WhereSyntax(column, true);
        }
    }

    /**
     * Join where clauses with conditional elements.
     */
    public class WhereJoinSyntax extends ExecutableExpression {

        /**
         * Adds another where clause joined with AND conditional.
         *
         * @param column The new column.
         * @return The where clause to add.
         */
        public WhereSyntax and(String column) {
            builder().append("AND ");
            return another(column);
        }

        /**
         * Adds another where clause joined with OR conditional.
         *
         * @param column The new column.
         * @return The where clause to add.
         */
        public WhereSyntax or(String column) {
            builder().append("OR ");
            return another(column);
        }

        /**
         * Creates another joined element.
         *
         * @param column The new column.
         * @return Another joined element.
         */
        private WhereSyntax another(String column) {
            return new WhereSyntax(column, false);
        }
    }

    /**
     * Expression is every sub grammar inside the query.
     */
    public abstract class Expression {
        /**
         * Prints the current value of this query.
         *
         * @return The current value of this query.
         */
        @Override
        public String toString() {
            return builder().toString();
        }
    }

    /**
     * Where clause.
     */
    public class WhereSyntax extends Expression {

        /**
         * Constructor for the new column implied in the where clause.
         *
         * @param column The column where this syntax will be applied.
         * @param firstClause Tells if it is the first clause.
         */
        public WhereSyntax(@Nullable String column, boolean firstClause) {
            if (firstClause) {
                builder().append("WHERE ");
            }
            if (column != null) {
                builder().append(column).append(" ");
            }
        }

        /**
         * Equals comparison.
         *
         * @param obj The object of the where.
         * @return The join where.
         */
        @NonNull
        public WhereJoinSyntax eq(@Nullable Object obj) {
            builder().append("= ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Not equals operation.
         *
         * @param obj The object to check.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax neq(@Nullable Object obj) {
            builder().append("!= ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Greater than operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax gt(@Nullable Object obj) {
            builder().append("> ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Greater than or equals operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax gte(@Nullable Object obj) {
            builder().append(">= ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Less than operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax lt(@Nullable Object obj) {
            builder().append("< ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Less than or equals operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax lte(@Nullable Object obj) {
            builder().append("<= ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Is operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax is(@Nullable Object obj) {
            builder().append("IS ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * The is not operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax isNot(@Nullable Object obj) {
            builder().append("IS NOT ").append(objToString(obj)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * The like operation for similar comparisons.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax like(@Nullable Object obj) {
            builder().append("LIKE '").append(objToString(obj)).append("' ");
            return new WhereJoinSyntax();
        }

        /**
         * The glob operation.
         *
         * @param obj The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax glob(@Nullable Object obj) {
            builder().append("GLOB '").append(objToString(obj)).append("' ");
            return new WhereJoinSyntax();
        }

        /**
         * Comparison with complex conjunctions for belonging.
         *
         * @param objs The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax in(@NonNull Object[] objs) {
            builder().append("IN ").append(objArrayToString(objs)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Comparison with complex conjunctions for not belonging.
         *
         * @param objs The object to compare.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax notIn(@Nullable Object[] objs) {
            builder().append("NOT IN ").append(objArrayToString(objs)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Value between two values.
         *
         * @param elem1 The first item.
         * @param elem2 The second item.
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax between(@Nullable Object elem1, Object elem2) {
            builder().append("BETWEEN ").append(objToString(elem1)).append(" AND ").append(objToString(elem2)).append(" ");
            return new WhereJoinSyntax();
        }

        /**
         * Checks if exists.
         *
         * @param otherQuery The query
         * @return The syntax.
         */
        @NonNull
        public WhereJoinSyntax exists(@NonNull Select otherQuery) {
            builder().append("(").append(otherQuery.toString()).append(") ");
            return new WhereJoinSyntax();
        }

        /**
         * Creates a string from an object array.
         *
         * @param objs The object array.
         * @return The string returned.
         */
        private String objArrayToString(Object[] objs) {
            StringBuilder statement = new StringBuilder();
            statement.append("(");
            boolean addComma = false;
            for (Object object : objs) {
                if (addComma) {
                    statement.append(",");
                }
                statement.append(objToString(object));
                addComma = true;
            }
            statement.append(")");
            return statement.toString();
        }


        /**
         * Prints the value of an object.
         *
         * @param obj ge object to print.
         * @return The string representation or 'NULL'.
         */
        @NonNull
        private String objToString(@Nullable Object obj) {
            if (obj != null) {
                if (obj instanceof String) {
                    return "'" + obj.toString() + "'";
                } else if (obj instanceof Date) {
                    return "date('" + ((Date) obj).getTime() + "')";
                } else {
                    return obj.toString();
                }
            } else {
                return "NULL";
            }
        }
    }

    /**
     * Executes the request on the database.
     *
     * @param database     The database.
     * @param descriptions Query descriptions.
     */
    public void on(@NonNull SQLiteDatabase database, @Nullable String... descriptions) {
        database.execSQL(print(descriptions));
    }
}
