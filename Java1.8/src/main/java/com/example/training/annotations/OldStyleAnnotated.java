package com.example.training.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Hints({
    @Hint("first"),
    @Hint("second")
})
class OldStyleAnnotated {

}

@Hint("first")
@Hint("second")
class NewStyleAnnotated {

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Hints.class)
@interface Hint {

  String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Hints {

  Hint[] value();
}
