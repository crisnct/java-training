package com.example.training.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.TypeVariable;

// 1) Annotation lives on the TYPE PARAMETER and is visible at runtime
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_PARAMETER)
@interface TTL {
    int minutes();
}

// 2) Generic component whose type parameter is annotated with @TTL
//    The annotation expresses a contract: "any T cached here uses this default TTL".
class Cache<@TTL(minutes = 10) T> {
    private final int defaultTtlMinutes;

    public Cache() {
        // Read the annotation from the class's type parameter at runtime
        TypeVariable<?> typeParam = Cache.class.getTypeParameters()[0];
        TTL ann = typeParam.getAnnotation(TTL.class);
        this.defaultTtlMinutes = (ann != null) ? ann.minutes() : 0;
        System.out.println("[Cache] Default TTL = " + defaultTtlMinutes + " minutes");
    }

    public void put(String key, T value) {
        // In a real cache you'd schedule expiry using defaultTtlMinutes
        System.out.println("PUT key=" + key + ", ttl=" + defaultTtlMinutes + "m, value=" + value);
    }

    public int getDefaultTtlMinutes() {
        return defaultTtlMinutes;
    }
}

// 3) Demo
public class TypeParameterAnnotation {
    public static void main(String[] args) {
        Cache<String> cache = new Cache<>();
        cache.put("greeting", "hello");
        System.out.println("Observed TTL: " + cache.getDefaultTtlMinutes() + " minutes");
    }
}
