package com.example.training;

import java.lang.reflect.Field;

/**
 * Demonstrates the impact of JEP 396 (Java 16) where internal JDK packages are strongly encapsulated and --illegal-access is disabled by default.
 * This snippet worked on Java 8 but fails on Java 16+ without extra flags.
 */
public class StrongEncapsulationStringDemo {

  public static void main(String[] args) throws Exception {
    System.out.println("java.version = " + System.getProperty("java.version"));

    String text = "hello";

    // Try to access private field "value" of java.lang.String
    Field valueField = String.class.getDeclaredField("value");
    valueField.setAccessible(true);  // <-- this is the problematic line on Java 16+

    Object rawValue = valueField.get(text);

    System.out.println("Reflection succeeded, rawValue type = " + rawValue.getClass());
  }
}
