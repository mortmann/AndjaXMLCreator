package com.mortmann.andja.creator.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	boolean required() default false;
    int order() default Integer.MAX_VALUE;
    @SuppressWarnings("rawtypes")
	Class type() default void.class;
    @SuppressWarnings("rawtypes")
	Class subType() default void.class;
    boolean id() default false;
    @SuppressWarnings("rawtypes")
	Class arraypos() default void.class;//make its so you can select the poplevel through combobox with enumnames
    //TODO For need make it so it can toggle between item/structure
    //TODO For need make it so it disables other fields
    //maybe with enums...
    boolean longtext() default false;
    boolean IsEffectable() default false;
    boolean RequiresEffectable() default false;

}
