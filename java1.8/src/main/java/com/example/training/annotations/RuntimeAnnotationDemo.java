package com.example.training.annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;

public class RuntimeAnnotationDemo {

    public static void main(String[] args) throws Exception {
        processAnnotations(Person.class);
    }

    // --------------------------
    // 1. Define annotation
    // --------------------------
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ValidateLength {
        int min() default 1;
        int max() default 10;
    }

    // --------------------------
    // 2. Use annotation
    // --------------------------
    static class Person {

        @ValidateLength(min = 3, max = 10)
        private String username = "Cristian";

        @ValidateLength(min = 5, max = 8)
        private String invalid = "X";   // this fails

        @ValidateLength(min = 2, max = 5)
        private String ok = "John";
    }

    // --------------------------
    // 3. Process annotation at runtime
    // --------------------------
    public static void processAnnotations(Class<?> clazz) throws Exception {

        Object instance = clazz.newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ValidateLength.class)) {

                ValidateLength rule = field.getAnnotation(ValidateLength.class);
                field.setAccessible(true);

                Object value = field.get(instance);

                if (!(value instanceof String)) {
                    System.out.println("Field " + field.getName() + " is not a String.");
                    continue;
                }

                String s = (String) value;

                if (s.length() < rule.min() || s.length() > rule.max()) {
                    System.out.println("❌ INVALID: Field '" + field.getName() +
                            "' has value '" + s + "' which violates length [" +
                            rule.min() + ", " + rule.max() + "]");
                } else {
                    System.out.println("✔ OK: Field '" + field.getName() +
                            "' = '" + s + "'");
                }
            }
        }
    }
}
