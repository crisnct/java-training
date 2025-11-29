package com.example.training.annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;

public class SerializationDemo {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Export { }

    static class Person {
        @Export
        private String name;          // runtime value

        @Export
        private int age;              // runtime value

        private String internalNotes; // not exported
    }

    public static void main(String[] args) throws Exception {
        Person p = new Person();
        p.name = "Cristian";
        p.age = 43;
        p.internalNotes = "private";

        System.out.println(toJson(p));
    }

    static String toJson(Object obj) throws Exception {
        StringBuilder sb = new StringBuilder("{");
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Export.class)) {
                f.setAccessible(true);
                sb.append("\"")
                  .append(f.getName())
                  .append("\":\"")
                  .append(f.get(obj))
                  .append("\",");
            }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }
}
