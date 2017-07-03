package com.mortmann.andja.creator.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	boolean required() default false;
    int order() default Integer.MAX_VALUE;
    @SuppressWarnings("rawtypes")
	Class type() default void.class;
}
