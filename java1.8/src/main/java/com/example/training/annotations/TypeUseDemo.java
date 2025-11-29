package com.example.training.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public class TypeUseDemo {

  public static void main(String[] args) {
    @NonEmpty String name = "Cristian";
    List<@NonEmpty String> names = java.util.Arrays.asList("A", "B");
    System.out.println(name + " " + names);
  }

  // Can be used on types, not just declarations
  @Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @interface NonEmpty {

  }
}
