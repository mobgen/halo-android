package com.mobgen.halo.android.framework.storage.database.dsl.queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Creates a select query to get some data from the database.
 */
public class Select extends Query {

    /**
     * Join policy annotation to constraint it.
     */
    @StringDef({NATURAL, LEFT, CROSS, LEFT_OUTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface JoinPolicy {
    }

    /**
     * Also known as inner join or natural inner join.
     */
    public static final String NATURAL = "NATURAL";
    /**
     * Left inner join.
     */
    public static final String LEFT = "LEFT";
    /**
     * Cross inner join or cartesian product.
     */
    public static final String CROSS = "CROSS";
    /**
     * The left outer join.
     */
    public static final String LEFT_OUTER = "LEFT OUTER";

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
     * Abstract inheritable executable sentence.
     */
    public abstract class ExecutableExpression {
        /**
         * Executes the sql in a database.
         *
         * @param database     The database.
         * @param descriptions Descriptions.
         * @return The raw retrieved.
         */
        public Cursor on(@NonNull SQLiteDatabase database, @Nullable String... descriptions) {
            return Select.this.on(database, descriptions);
        }

        /**
         * Executes the sql in the helper by getting the database.
         *
         * @param storage      The database.
         * @param descriptions Descriptions.
         * @return The raw.
         */
        public Cursor on(@NonNull HaloDataLite storage, @Nullable String... descriptions) {
            return on(storage.getDatabase(), descriptions);
        }
    }

    /**
     * Constructor for the select.
     */
    private Select() {
        super();
        builder().append("SELECT ");
    }

    /**
     * Where clause.
     */
    public class WhereSyntax extends Expression {

        /**
         * Constructor for the new column implied in the where clause.
         *
         * @param column The column where this syntax will be applied.
         * @param firstClause True if it is the first clause, false otherwise.
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
            if(objs.length != 0) {
                for (Object object : objs) {
                    if (addComma) {
                        statement.append(",");
                    }
                    statement.append(objToString(object));
                    addComma = true;
                }
            }else{
                statement.append("\"\"");
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
         * Adds a order by clause to the query.
         *
         * @param column The column.
         * @return The order clause to apply.
         */
        public OrderSyntax order(String column){
            builder().append(" ORDER BY " + column);
            return new OrderSyntax();
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
     * Adds columns as a projection.
     */
    public class ColumnsSyntax extends Expression {

        /**
         * The columns syntax.
         *
         * @param columns The columns to execute.
         */
        private ColumnsSyntax(@Nullable String[] columns) {
            if (columns == null) {
                builder().append("* ");
            } else {
                boolean hasComma = false;
                for (String column : columns) {
                    if (hasComma) {
                        builder().append(", ");
                    }
                    builder().append(column);
                    hasComma = true;
                }
                builder().append(" ");
            }
        }

        /**
         * The table to query.
         *
         * @param table The table to query.
         * @return The syntax for From.
         */
        public FromSyntax from(@NonNull Class<? extends HaloTable> table) {
            return new FromSyntax(table);
        }
    }


    /**
     * Order clause.
     */
    public class OrderSyntax extends ExecutableExpression {

        /**
         * Set order by desc.
         *
         * @return The order clause
         */
        public OrderSyntax desc() {
            builder().append(" DESC ");
            return new OrderSyntax();
        }

        /**
         * Set order by asc.
         *
         * @return The order clause
         */
        public OrderSyntax asc() {
            builder().append(" ASC ");
            return new OrderSyntax();
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
         * Joins this table with another.
         *
         * @param table The table that will be queried.
         * @return The natural join syntax.
         */
        public JoinSyntax joinOn(Class<? extends HaloTable> table) {
            return new JoinSyntax(table);
        }

        /**
         * Joins this table with another.
         *
         * @param joinPolicy The join policy that will be used.
         * @param table      The table that will be queried.
         * @param fieldFrom  The field from used for the join.
         * @param fieldTo    The field to used for the join.
         * @return The join syntax.
         */
        public JoinSyntax joinOn(@NonNull @JoinPolicy String joinPolicy, Class<? extends HaloTable> table, String fieldFrom, String fieldTo) {
            return new JoinSyntax(joinPolicy, table, fieldFrom, fieldTo);
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


        /**
         * Adds a order by clause to the query.
         *
         * @param column The column.
         * @return The order clause to apply.
         */
        public OrderSyntax order(String column){
            builder().append(" ORDER BY " + column);
            return new OrderSyntax();
        }
    }

    /**
     * Join syntax.
     */
    public class JoinSyntax extends ExecutableExpression {

        /**
         * Joins syntax grammar.
         *
         * @param table The table class.
         */
        private JoinSyntax(Class<? extends HaloTable> table) {
            builder().append("NATURAL JOIN ");
            builder().append(ORMUtils.getTableName(table));
        }

        /**
         * The join syntax grammar.
         *
         * @param joinPolicy The join policy.
         * @param table      The table class.
         * @param fieldFrom  The Left field.
         * @param fieldTo    The right field.
         */
        private JoinSyntax(@NonNull @JoinPolicy String joinPolicy, @NonNull Class<? extends HaloTable> table, @NonNull String fieldFrom, @NonNull String fieldTo) {
            builder().append(joinPolicy);
            builder().append(" JOIN ");
            builder().append(ORMUtils.getTableName(table));
            builder().append(" ON ").append(fieldFrom).append(" = ").append(fieldTo).append(" ");
        }
    }

    /**
     * The columns items to select.
     *
     * @param columns The columns.
     * @return The syntax grammar for the columns.
     */
    public static ColumnsSyntax columns(@NonNull String... columns) {
        return new Select().columnsInner(columns);
    }

    /**
     * Selects all the columns.
     *
     * @return The columns grammar for the columns.
     */
    public static ColumnsSyntax all() {
        return new Select().columnsInner(null);
    }

    /**
     * Creates the columns syntax.
     *
     * @param columns The columns.
     * @return The syntax for columns.
     */
    private ColumnsSyntax columnsInner(@Nullable String[] columns) {
        return new ColumnsSyntax(columns);
    }

    /**
     * Executes the request on the database.
     *
     * @param database     The database.
     * @param descriptions Descriptions.
     * @return The raw for this select.
     */
    public Cursor on(@NonNull SQLiteDatabase database, @Nullable String... descriptions) {
        return database.rawQuery(print(descriptions), null);
    }
}
