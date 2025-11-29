package com.example.training.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(Roles.class)
@interface Role {
  String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Roles {
  Role[] value();
}

@Role("admin")
@Role("auditor")
class Secured {

}

public class RepeatableDemo {

  public static void main(String[] args) {
    for (Role r : Secured.class.getAnnotationsByType(Role.class)) {
      System.out.println(r.value());
    }
  }
}
