// file: com/example/training/annotations/processor/ValidAge.java
package com.example.training.annotations.processor.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)      // needed for runtime validation
@Target(ElementType.FIELD)
public @interface ValidAgeRuntime {

    int min() default 18;

    int max() default 80;

}
