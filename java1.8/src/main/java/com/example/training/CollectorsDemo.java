package com.example.training;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorsDemo {

  public static void main(String[] args) {
    List<String> words = Arrays.asList(
        "java", "test", "stream", "test", "future", "java"
    );

    // toList
    List<String> upper =
        words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());

    System.out.println("toList: " + upper);

    // joining
    String joined =
        words.stream()
            .distinct()
            .collect(Collectors.joining(", "));

    System.out.println("joining: " + joined);

    // groupingBy
    Map<Integer, List<String>> byLength =
        words.stream()
            .collect(Collectors.groupingBy(String::length));

    System.out.println("groupingBy: " + byLength);

    // counting + groupingBy
    Map<String, Long> freq =
        words.stream()
            .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

    System.out.println("frequency: " + freq);
  }
}
