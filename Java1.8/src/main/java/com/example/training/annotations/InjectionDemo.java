package com.example.training.annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;

public class InjectionDemo {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Inject { }

    static class Config {
        String getUrl() { return "jdbc:mysql://localhost/demo"; }
    }

    static class App {
        @Inject
        private Config config;  // injected at runtime, not a constant
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
        injectDependencies(app);
        System.out.println(app.config.getUrl());
    }

    static void injectDependencies(Object obj) throws Exception {
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Inject.class)) {
                f.setAccessible(true);
                Object instance = f.getType().newInstance();
                f.set(obj, instance);
            }
        }
    }
}
