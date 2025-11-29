package com.example.training.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
@interface ClassRetentionTag { }

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface RuntimeRetentionTag { }

public class AnnotationsVisibility {

    // Annotate fields
    @ClassRetentionTag
    private String title = "demo";

    @RuntimeRetentionTag
    private int version = 8;

    public static void main(String[] args) {
        AnnotationsVisibility instance = new AnnotationsVisibility();
        inspectFieldAnnotations(instance.getClass());
    }

    private static void inspectFieldAnnotations(Class<?> type) {
        Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            boolean hasClassRetention = field.getAnnotation(ClassRetentionTag.class) != null;
            boolean hasRuntimeRetention = field.getAnnotation(RuntimeRetentionTag.class) != null;

            // Expected: false for CLASS, true for RUNTIME
            System.out.println("Field: " + field.getName());
            System.out.println("  @ClassRetentionTag visible at runtime?  " + hasClassRetention);
            System.out.println("  @RuntimeRetentionTag visible at runtime? " + hasRuntimeRetention);
        }
    }
}
