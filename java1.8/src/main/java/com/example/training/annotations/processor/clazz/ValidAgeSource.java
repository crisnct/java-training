package com.example.training.annotations.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ✔ Exists only during compilation
 * ✔ Accessible to the annotation processor
 * ✔ Invisible at runtime
 */
@Retention(RetentionPolicy.SOURCE)      // needed for runtime validation
@Target(ElementType.FIELD)
public @interface ValidAgeSource {
  int min() default 18;

  int max() default 80;

}
