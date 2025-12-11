package com.example.training;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@formatter:off
/**
 * Java 22: Unnamed variables & unnamed pattern variables (`_`)
 * - Local discard:      var _ = computeSideEffect();
 * - Lambda discard:     map.forEach((_, v) -> total += v);
 * - Catch discard:      catch (NumberFormatException _) { ... }
 * - Pattern discard:    switch (o) { case User(_, int age, _) -> ... }
 *
 * Build: javac UnnamedVariablesDemo.java
 * Run:   java UnnamedVariablesDemo
 */
//@formatter:on
public class UnnamedVariablesDemo {

  public static void main(String[] args) {
    // 1) Local discard: keep side-effects, ignore the value
    var _ = computeSideEffect();

    // 2) Lambda discard in Map.forEach: ignore key, only sum values
    Map<String, Integer> stockBySku = new HashMap<>();
    stockBySku.put("A-100", 5);
    stockBySku.put("B-200", 12);
    stockBySku.put("C-300", 3);

    final int[] total = {0};
    stockBySku.forEach((_, qty) -> total[0] += qty);
    System.out.println("Total stock = " + total[0]);

    // 3) Enhanced-for + discard: count items; show explicit discard
    int count = 0;
    for (var item : List.of("alpha", "beta", "gamma")) {
      var _unused = item; // demonstrate explicit throwaway binding
      count++;
    }
    System.out.println("Items counted = " + count);

    // 4) Catch discard: you don’t need the exception object
    try {
      Integer.parseInt("not-a-number");
    } catch (NumberFormatException _) {
      System.out.println("Parsing failed (exception ignored by design).");
    }

    // 5) Unnamed pattern variables: deconstruct a record, only use selected components
    Object o = new User("Alice", 42, "alice@example.com");
    System.out.println("User age = " + extractAge(o));

    // Another pattern example with a simple Point record
    Object shape = new Point(7, 11);
    System.out.println("Point Y = " + onlyY(shape));
  }

  // Records used for pattern deconstruction demo
  record User(String name, int age, String email) {

  }

  record Point(int x, int y) {

  }

  // Switch with unnamed pattern variables: match and ignore fields we don't need
  private static int extractAge(Object o) {
    return switch (o) {
      case User(_, int age, _) -> age;  // ignore name and email
      default -> -1;
    };
  }

  // Another pattern: only care about y; ignore x
  private static int onlyY(Object o) {
    return switch (o) {
      case Point(_, int y) -> y;
      default -> 0;
    };
  }

  private static int computeSideEffect() {
    System.out.println("Side-effect done.");
    return 123; // value intentionally discarded by caller
  }
}
