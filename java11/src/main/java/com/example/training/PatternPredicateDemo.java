package com.example.training;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatternPredicateDemo {

  public static void main(String[] args) {
    Pattern p = Pattern.compile("^a.*");

    // Convert regex → Predicate<String>
    var startsWithA = p.asMatchPredicate();

    List<String> items = List.of("apple", "banana", "avocado", "cat");
    var filtered = items.stream()
        .filter(startsWithA)
        .collect(Collectors.toList());

    System.out.println(filtered);  // [apple, avocado]
  }
}
