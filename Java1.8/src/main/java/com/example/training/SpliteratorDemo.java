package com.example.training;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;

/**
 * It attempts to break a data source into independent chunks so multiple threads can process them in parallel.
 */
public class SpliteratorDemo {

  public static void main(String[] args) {
    List<String> names = Arrays.asList("Ana", "Bob", "Cristian", "Dana", "Eli", "Andreea", "Alexandra", "Corina");

    Spliterator<String> sp1 = names.spliterator();
    // Try to split it
    Spliterator<String> sp2 = sp1.trySplit();

    System.out.println("--- Spliterator 1 ---");
    sp1.forEachRemaining(System.out::println);

    if (sp2 != null) {
      Spliterator<String> sp3 = sp2.trySplit();
      if (sp3 != null) {
        System.out.println("--- Spliterator 3 ---");
        sp3.forEachRemaining(System.out::println);
      } else {
          System.out.println("Could not split.");
      }
      System.out.println("--- Spliterator 2 ---");
      sp2.forEachRemaining(System.out::println);
    } else {
      System.out.println("Could not split.");
    }
  }
}
