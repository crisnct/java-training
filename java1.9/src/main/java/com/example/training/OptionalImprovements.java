package com.example.training;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class OptionalImprovements {

  public static void main(String[] args) {
    Optional<String> opt = Optional.of("Cristian");
    Optional<String> empty = Optional.empty();

    // ifPresentOrElse
    opt.ifPresentOrElse(
        v -> System.out.println("Value: " + v),
        () -> System.out.println("Empty")
    );

    // or()
    Optional<String> result = empty.or(() -> Optional.of("Backup"));
    System.out.println("Result: " + result.get());

    // stream()
    Stream.of(opt, empty)
        .flatMap(Optional::stream)
        .forEach(System.out::println);

    Optional<String> name2 = Optional.of("Cristian");
    name2.stream()
        .map(String::toUpperCase)
        .forEach(System.out::println);

    Optional<List<String>> name = Optional.of(List.of("Cristian", "Andreea", "Alexandra"));
    name.stream()
        .flatMap(p -> Arrays.stream(p.toArray()))
        .forEach(System.out::println);
  }
}
