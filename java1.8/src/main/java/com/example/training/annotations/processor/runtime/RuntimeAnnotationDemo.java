package com.example.training.annotations.processor.runtime;

import com.example.training.annotations.processor.Person;
import java.lang.reflect.Field;

public class RuntimeAnnotationDemo {

  public static void main(String[] args) throws Exception {
    Person p = new Person();
    p.setName("Cristian");
    p.setAge(25);
    validateAge(p);
    p.setAge(3);
    validateAge(p);
  }

  static void validateAge(Person target) {
    try {
      Class<?> clazz = target.getClass();
      Field field = clazz.getDeclaredField("age");
      if (!field.isAnnotationPresent(ValidAgeRuntime.class)) {
        // no annotation, nothing to validate
        return;
      }

      ValidAgeRuntime validAge = field.getAnnotation(ValidAgeRuntime.class);
      int min = validAge.min();
      int max = validAge.max();

      if (target.getAge() < min || target.getAge() > max) {
        System.err.println("Age validation failed for age field : " + target.getAge() + " not in [" + min + ", " + max + "]");
      } else {
        System.out.println("Age validation OK for age field: " + target.getAge());
      }
    } catch (NoSuchFieldException e) {
      System.err.println("Field age not found on " + target.getClass());
    }
  }

}
