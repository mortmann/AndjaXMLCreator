package com.mortmann.andja.creator.util;

import java.lang.reflect.Field;
import java.util.Comparator;

public class OrderEr implements Comparator<Field> {
    @Override
    public int compare(Field o1, Field o2) {
        FieldInfo or1 = o1.getAnnotation(FieldInfo.class);
        FieldInfo or2 = o2.getAnnotation(FieldInfo.class);
        // nulls last
        if (or1 != null && or2 != null) {
            return or1.order() - or2.order();
        } 
        if (or1 != null && or2 == null) {
            return Integer.MIN_VALUE + or1.order();
        } 
        if (or1 == null && or2 != null) {
            return Integer.MAX_VALUE - or2.order();
        }
        return o1.getName().compareTo(o2.getName());
    }
}

