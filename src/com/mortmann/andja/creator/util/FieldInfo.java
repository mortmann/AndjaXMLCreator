package com.mortmann.andja.creator.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("rawtypes")
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	boolean required() default false;
    int order() default 500;
	Class compareType() default void.class;
	Class mainType() default void.class;
	Class subType() default void.class;
    boolean id() default false;
	Class arraypos() default void.class;//make its so you can select the poplevel through combobox with enumnames
    //TODO For need make it so it can toggle between item/structure
    //TODO For need make it so it disables other fields
    //maybe with enums...
    boolean longtext() default false;
    boolean IsEffectable() default false;
    boolean RequiresEffectable() default false;
	boolean ignore() default false;
	String First2DName() default "";
	String Second2DName() default "";
	float Minimum() default Integer.MIN_VALUE;
	float Maximum() default Integer.MAX_VALUE;
}
