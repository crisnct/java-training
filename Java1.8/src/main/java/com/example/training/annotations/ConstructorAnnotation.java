package com.example.training.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

// ===== Annotation: runtime, constructor-target =====
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@interface InjectCtor {
    boolean primary() default true; // in case multiple ctors are annotated
}

// ===== Example dependencies =====
class Database {
    private final String url;
    Database(String url) { this.url = url; }
    String getUrl() { return url; }
}

class ServiceConfig {
    private final int maxConnections;
    ServiceConfig(int maxConnections) { this.maxConnections = maxConnections; }
    int getMaxConnections() { return maxConnections; }
}

// ===== Service with multiple constructors; one is annotated =====
class UserService {
    private final Database database;
    private final ServiceConfig config;

    public UserService() {
        this.database = new Database("jdbc:noop://default");
        this.config = new ServiceConfig(5);
        System.out.println("UserService created via NO-ARG constructor (fallback).");
    }

    @InjectCtor(primary = true)
    public UserService(Database database, ServiceConfig config) {
        this.database = database;
        this.config = config;
        System.out.println("UserService created via @InjectCtor(Database, ServiceConfig).");
    }

    String info() {
        return "UserService{db=" + database.getUrl() + ", maxConn=" + config.getMaxConnections() + "}";
    }
}

// ===== Minimal DI container that prefers @InjectCtor =====
class SimpleContainer {
    private final Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();

    public <T> void registerInstance(Class<T> type, T instance) {
        singletons.put(type, instance);
    }

    public <T> T create(Class<T> type) {
        try {
            Constructor<?>[] ctors = type.getDeclaredConstructors();
            Constructor<?> preferred = null;

            // 1) Prefer @InjectCtor(primary=true)
            for (int i = 0; i < ctors.length; i++) {
                InjectCtor ann = ctors[i].getAnnotation(InjectCtor.class);
                if (ann != null && ann.primary()) {
                    preferred = ctors[i];
                    break;
                }
            }
            // 2) If none primary, pick any @InjectCtor
            if (preferred == null) {
                for (int i = 0; i < ctors.length; i++) {
                    if (ctors[i].isAnnotationPresent(InjectCtor.class)) {
                        preferred = ctors[i];
                        break;
                    }
                }
            }
            // 3) Fallback: no-arg constructor
            if (preferred == null) {
                for (int i = 0; i < ctors.length; i++) {
                    if (ctors[i].getParameterTypes().length == 0) {
                        preferred = ctors[i];
                        break;
                    }
                }
            }
            if (preferred == null) {
                throw new IllegalStateException("No usable constructor found for " + type.getName());
            }

            preferred.setAccessible(true);
            Class<?>[] paramTypes = preferred.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                Object dep = resolve(paramTypes[i]);
                if (dep == null) {
                    throw new IllegalStateException("Unresolved dependency: " + paramTypes[i].getName() +
                            " for " + type.getName());
                }
                args[i] = dep;
            }

            @SuppressWarnings("unchecked")
            T instance = (T) preferred.newInstance(args);
            return instance;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot instantiate " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Constructor not accessible for " + type.getName(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Constructor threw: " + e.getTargetException(), e);
        }
    }

    private Object resolve(Class<?> type) {
        return singletons.get(type);
    }
}

// ===== Demo runner =====
public class ConstructorAnnotation {
    public static void main(String[] args) {
        SimpleContainer container = new SimpleContainer();
        container.registerInstance(Database.class, new Database("jdbc:postgresql://db:5432/app"));
        container.registerInstance(ServiceConfig.class, new ServiceConfig(42));

        UserService service = container.create(UserService.class);
        System.out.println(service.info());
    }
}
