package com.mobgen.halo.android.framework.storage.database.dsl.annotations;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The column of a database.
 */
@Keep
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * The type of the field.
     */
    enum Type {
        INTEGER,
        REAL,
        TEXT,
        BLOB,
        DATE,
        BOOLEAN,
        NUMERIC
    }

    /**
     * The type of the field.
     *
     * @return The type.
     */
    Type type();

    /**
     * True if unique, false otherwise.
     *
     * @return True if unique, false otherwise.
     */
    boolean unique() default false;

    /**
     * References another column.
     *
     * @return The table referenced.
     */
    Class<? extends HaloTable> references() default DEFAULT.class;

    /**
     * The column reference.
     *
     * @return The string with the reference.
     */
    String columnReference() default "";

    /**
     * Determines if a field is a primary key.
     *
     * @return True if primary key, false otherwise.
     */
    boolean isPrimaryKey() default false;

    /**
     * Default implementation for the references annotation.
     */
    final class DEFAULT implements HaloTable {
    }
}

