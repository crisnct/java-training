package com.example.training;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

//@formatter:off
/**
 * Demonstrates JEP 416 effects:
 *
 * Public reflection API remains unchanged, but internally
 * Method.invoke(), Constructor.newInstance(), and Field access
 * now leverage java.lang.invoke method-handle infrastructure.
 *
 * This code will run unchanged on Java 8–17,
 * but under Java 18 it benefits from the new reflection backend.
 */
//@formatter:on
public class ReflectionDemo {

  public static class Person {

    private final String name;

    public Person(String name) {
      this.name = name;
    }

    private String greet(String msg) {
      return name + " says: " + msg;
    }
  }

  public static void main(String[] args) throws Exception {

    // Create instance via reflection
    Constructor<Person> ctor = Person.class.getDeclaredConstructor(String.class);
    Person p = ctor.newInstance("Ada");

    // Access private method via reflection
    Method greet = Person.class.getDeclaredMethod("greet", String.class);
    greet.setAccessible(true);

    // Invoke it on the instance
    Object result = greet.invoke(p, "Hello from Java 18!");

    System.out.println(result);
  }
}
