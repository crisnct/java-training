package com.example.training;

import java.util.List;
import java.util.Map;

public class InstanceofPatternMatchingDemo {

  public static void main(String[] args) {
    Object s = "Hello Java 16";
    Object i = Integer.valueOf(42);
    Object list = List.of("a", "b", "c");
    Object map = Map.of("k1", 1, "k2", 2);
    Object unknown = 3.14;

    System.out.println("=== Basic pattern matching ===");
    printUpperCaseIfString(s);
    printUpperCaseIfString(i);

    System.out.println();
    System.out.println("=== Safe length example ===");
    System.out.println("Length of s: " + safeLength(s));
    System.out.println("Length of i: " + safeLength(i));

    System.out.println();
    System.out.println("=== Combined conditions ===");
    printOnlyLongStrings("hi");
    printOnlyLongStrings("pattern matching");
    printOnlyLongStrings(123);

    System.out.println();
    System.out.println("=== Polymorphic processing ===");
    process(list);
    process(map);
    process(unknown);
  }

  /**
   * Before Java 16:
   * <p>
   * if (obj instanceof String) { String text = (String) obj; ... }
   * <p>
   * Now we use pattern matching directly.
   */
  private static void printUpperCaseIfString(Object obj) {
    if (obj instanceof String text) {
      System.out.println("String in upper case: " + text.toUpperCase());
    } else {
      System.out.println("Not a String: " + obj);
    }
  }

  /**
   * Simple method that returns the length only if it's a String. No explicit cast needed.
   */
  private static int safeLength(Object obj) {
    if (obj instanceof String s) {
      return s.length();
    }
    return -1;
  }

  /**
   * Pattern variable (s) is available in the rest of the condition.
   */
  private static void printOnlyLongStrings(Object obj) {
    if (obj instanceof String s && s.length() > 5) {
      System.out.println("Long string: " + s);
    } else {
      System.out.println("Not a long string: " + obj);
    }
  }

  /**
   * More realistic usage: branching behavior based on runtime type, without repetitive casts.
   */
  private static void process(Object resource) {
    if (resource instanceof List<?> list) {
      System.out.println("Processing List, size = " + list.size());
    } else if (resource instanceof Map<?, ?> map) {
      System.out.println("Processing Map, keys = " + map.keySet());
    } else if (resource instanceof String s) {
      System.out.println("Processing String, reversed = " + new StringBuilder(s).reverse());
    } else {
      System.out.println("Unknown type: " + resource);
    }
  }
}
