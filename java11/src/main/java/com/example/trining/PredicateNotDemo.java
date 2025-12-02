package com.example.trining;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateNotDemo {

  public static void main(String[] args) {
    List<String> items = List.of("a", "", "b", "   ", "c");

    List<String> nonBlank = items.stream()
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.toList());
    //Equivalent to
    //    nonBlank = items.stream()
    //        .filter(str -> new Predicate<String>() {
    //          @Override
    //          public boolean test(String s) {
    //            return s.isBlank();
    //          }
    //        }.test(str))
    //        .collect(Collectors.toList());
    System.out.println(nonBlank); // [a, b, c]
  }
}
