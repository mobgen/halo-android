package com.mobgen.halo.android.content.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) @Retention(RetentionPolicy.SOURCE)
public @interface HaloQuery {
    String name();
    String query();
}
