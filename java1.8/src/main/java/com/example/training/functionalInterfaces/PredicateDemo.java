package com.example.training.functionalInterfaces;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class PredicateDemo {

  public static void main(String[] args) {
    System.out.println("Simple test");
    Predicate<String> isLong = text -> text.length() > 5;
    System.out.println(isLong.test("Java"));        // false
    System.out.println(isLong.test("Cristian"));    // true

    System.out.println("\nIntPredicate test");
    IntPredicate isEven = n -> n % 2 == 0;
    System.out.println("isEven(4): " + isEven.test(4));

    System.out.println("\nComplex test");
    List<String> names = Arrays.asList(
        "Cristian", "Ana", "Alex", "Bob", "Andrei", "Ion"
    );

    // Base predicates
    Predicate<String> longerThan3 = s -> s.length() > 3;
    Predicate<String> startsWithA = s -> s.startsWith("A");
    Predicate<String> endsWithN = s -> s.endsWith("n");

    // Chained predicate: (length > 3 AND starts with A) OR ends with n
    Predicate<String> combined =
        longerThan3
            .and(startsWithA)
            .or(endsWithN);

    // Apply the chain
    names.stream()
        .filter(combined)
        .forEach(System.out::println);
  }
}
