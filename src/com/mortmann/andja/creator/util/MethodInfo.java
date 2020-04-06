package com.mortmann.andja.creator.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MethodInfo {
	String BelongingVariable() default "";
	String Title() default "";
}
