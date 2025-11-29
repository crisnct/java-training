package com.example.training.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/** Annotation: câmpul primește valoare din env / system properties la runtime. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface FromEnv {
    String value();              // cheie (ex: "DB_URL")
    boolean required() default true;
    boolean masked() default false; // dacă true, se maschează în loguri/toString
}

/** Config aplicație: marchezi câmpurile care vin din env. */
final class AppConfig {
    @FromEnv(value = "DB_URL", required = true, masked = true)
    private String databaseUrl;

    @FromEnv(value = "PORT", required = true)
    private int serverPort;

    @FromEnv(value = "DEBUG", required = false)
    private boolean debugEnabled;

    public String getDatabaseUrl() { return databaseUrl; }
    public int getServerPort() { return serverPort; }
    public boolean isDebugEnabled() { return debugEnabled; }
}

/** Binder simplu care injectează valori în câmpurile marcate cu @FromEnv. */
final class EnvBinder {

    private EnvBinder() { }

    public static <T> T bindFromEnv(Class<T> type) {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            T instance = ctor.newInstance();

            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                FromEnv ann = field.getAnnotation(FromEnv.class);
                if (ann == null) {
                    continue;
                }

                String key = ann.value();
                String raw = firstNonNull(System.getProperty(key), System.getenv(key));

                if (raw == null) {
                    if (ann.required()) {
                        throw new IllegalStateException("Missing required env key: " + key);
                    } else {
                        continue;
                    }
                }

                Object converted = convert(raw, field.getType(), key);
                field.setAccessible(true);
                field.set(instance, converted);
            }
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to bind config: " + e.getMessage(), e);
        }
    }

    /** toString “safe”: câmpurile marcate masked=true apar ca **** */
    public static String safeToString(Object obj) {
      if (obj == null) {
        return "null";
      }
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getSimpleName()).append('{');

        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            sb.append(f.getName()).append('=');
            try {
                FromEnv ann = f.getAnnotation(FromEnv.class);
                if (ann != null && ann.masked()) {
                    sb.append("****");
                } else {
                    Object val = f.get(obj);
                    sb.append(String.valueOf(val));
                }
            } catch (IllegalAccessException e) {
                sb.append("<inaccessible>");
            }
          if (i + 1 < fields.length) {
            sb.append(", ");
          }
        }
        sb.append('}');
        return sb.toString();
    }

    private static Object convert(String raw, Class<?> targetType, String key) {
        if (targetType == String.class) {
            return raw;
        }
        if (targetType == int.class || targetType == Integer.class) {
            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid int for key " + key + ": " + raw);
            }
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return "true".equalsIgnoreCase(raw) || "1".equals(raw.trim());
        }
        throw new IllegalStateException("Unsupported type for key " + key + ": " + targetType.getName());
    }

    private static String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}

/** Demo minimal. Setăm System properties pentru simplitate (env real rămâne neschimbat). */
public class EnvBuinder {
    public static void main(String[] args) {
        // Exemplu: în producție ai variabile de mediu; aici folosim System properties pt. demo.
        System.setProperty("DB_URL", "jdbc:postgresql://db:5432/app");
        System.setProperty("PORT", "8080");
        System.setProperty("DEBUG", "true");

        AppConfig cfg = EnvBinder.bindFromEnv(AppConfig.class);

        // Log prietenos: valorile sensibile (DB_URL) apar mascate
        System.out.println(EnvBinder.safeToString(cfg));

        // Codul folosește valorile reale
        System.out.println("Port bound: " + cfg.getServerPort());
        System.out.println("Debug mode: " + cfg.isDebugEnabled());
        // Evităm să afișăm databaseUrl în clar în loguri
    }
}
