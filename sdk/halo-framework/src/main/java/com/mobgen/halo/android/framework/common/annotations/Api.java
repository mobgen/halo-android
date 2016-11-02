package com.mobgen.halo.android.framework.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every class, method, field or constructor annotated with this annotation means that this element is not likely to
 * change in the current version so you can use it safely within the same major.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Api {
    /**
     * The value of the version to which this method belongs.
     *
     * @return The value.
     */
    double value();
}
