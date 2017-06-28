package com.mortmann.andja.creator.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
	boolean required() default false;
    int order();
    
}
