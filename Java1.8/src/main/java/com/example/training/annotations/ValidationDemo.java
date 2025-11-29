package com.example.training.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class ValidationDemo {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ValidateLength {

    int min();

    int max();
  }

  static class User {

    @ValidateLength(min = 3, max = 10)
    private String username;   // NOT a constant

    @ValidateLength(min = 5, max = 20)
    private String bio;        // runtime value
  }

  public static void main(String[] args) throws Exception {
    User u = new User();
    u.username = "Cristian";
    u.bio = "Java developer";
    u.bio = "dev";

    validateObject(u);
  }

  private static void validateObject(Object obj) throws Exception {
    for (Field f : obj.getClass().getDeclaredFields()) {
      if (f.isAnnotationPresent(ValidateLength.class)) {
        f.setAccessible(true);
        ValidateLength rule = f.getAnnotation(ValidateLength.class);
        String value = (String) f.get(obj);

        if (value == null ||
            value.length() < rule.min() ||
            value.length() > rule.max()) {
          throw new IllegalArgumentException(
              "Field '" + f.getName() + "' violates @ValidateLength");
        }

        System.out.println("OK: " + f.getName() + " = " + value);
      }
    }
  }
}
