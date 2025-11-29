package com.example.training.annotations.processor.clazz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ✔ Annotation is stored in bytecode. Stored in .class file
 * ✔ Annotation can be used by annotation processors
 * ✔ Annotation cannot be used at runtime via reflection
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ValidAgeClass {

    int min() default 18;

    int max() default 80;

}
