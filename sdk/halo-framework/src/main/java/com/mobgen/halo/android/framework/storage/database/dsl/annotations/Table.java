package com.mobgen.halo.android.framework.storage.database.dsl.annotations;

import android.support.annotation.Keep;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determines the priority of the modules.
 */
@Keep
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * The name of the table.
     *
     * @return The name of the table.
     */
    String value();

    /**
     * Tells if the table contains multiple primary keys.
     *
     * @return True if it contains multiple primary keys. False otherwise.
     */
    boolean multipleKeys() default false;
}
