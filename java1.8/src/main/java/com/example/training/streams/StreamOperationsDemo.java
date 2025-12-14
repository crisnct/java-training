package com.example.training.streams;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamOperationsDemo {

  public static void main(String[] args) {

    List<String> words = Arrays.asList(
        "java", "stream", "cristian", "lambda", "java", "API", "future", "stream"
    );
    Stream<String> s1 = words.stream()
        .filter(w -> w.length() > 4)
        .map(String::toUpperCase)
        //explode words into characters
        .flatMap(w -> Arrays.stream(w.split("")))
        .distinct()
        .sorted()
        //debug print
        .peek(ch -> System.out.println("Peek: " + ch))
        //skip first 2
        .skip(2)
        .limit(10);
    System.out.println("\n--- forEach ---");
    s1.forEach(System.out::println);

    List<List<Integer>> batches = Arrays.asList(
        Arrays.asList(1, 2),
        Arrays.asList(3, 4, 5),
        Collections.singletonList(6)
    );
    List<Integer> all2 = batches.stream()
        .flatMap(List::stream)
        .collect(Collectors.toList());
    System.out.println("flatmsp demo: " + all2); // [1, 2, 3, 4, 5, 6]

    // =====================================================================
    // TERMINAL OPERATIONS
    // =====================================================================

    // Create a fresh stream for terminal examples (streams are single-use)
    Stream<String> again = words.stream().filter(w -> w.length() > 4);

    // collect
    System.out.println("\n--- collect ---");
    List<String> collected = again.collect(Collectors.toList());
    System.out.println(collected);

    // reduce
    System.out.println("\n--- reduce ---");
    Optional<String> reduced = words.stream().reduce((a, b) -> a + "-" + b);
    reduced.ifPresent(System.out::println);

    // count
    System.out.println("\n--- count ---");
    long count = words.stream().count();
    System.out.println(count);

    // min & max (length comparator)
    System.out.println("\n--- min & max ---");
    words.stream().min(Comparator.comparingInt(String::length))
        .ifPresent(v -> System.out.println("Min: " + v));
    words.stream().max(Comparator.comparingInt(String::length))
        .ifPresent(v -> System.out.println("Max: " + v));

    // anyMatch, allMatch, noneMatch
    System.out.println("\n--- match operations ---");
    boolean any = words.stream().anyMatch(w -> w.startsWith("j"));
    boolean all = words.stream().allMatch(w -> w.length() > 2);
    boolean none = words.stream().noneMatch(w -> w.startsWith("zzz"));

    System.out.println("anyMatch: " + any);
    System.out.println("allMatch: " + all);
    System.out.println("noneMatch: " + none);

    // findFirst
    System.out.println("\n--- findFirst ---");
    words.stream().filter(w -> w.length() > 4)
        .findFirst()
        .ifPresent(System.out::println);

    // findAny
    System.out.println("\n--- findAny ---");
    words.stream().filter(w -> w.length() > 4)
        .findAny()
        .ifPresent(System.out::println);
  }
}
