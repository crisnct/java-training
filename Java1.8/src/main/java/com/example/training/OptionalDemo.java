package com.example.training;

import java.util.Optional;

public class OptionalDemo {

  public static void main(String[] args) {
    // Create Optional values
    Optional<String> name = Optional.of("Cristian");
    Optional<String> empty = Optional.empty();
    Optional<String> nullableValue = Optional.ofNullable(getNullable());

    // Retrieve values
    System.out.println("Name: " + name.get());
    System.out.println("Nullable (orElse): " + nullableValue.orElse("Unknown"));
    System.out.println("Empty (orElse): " + empty.orElse("No value"));

    // Map / transform value if present
    Optional<Integer> nameLength = name.map(String::length);
    System.out.println("Name length: " + nameLength.orElse(0));

    // If present, run action
    name.ifPresent(n -> System.out.println("Uppercase: " + n.toUpperCase()));

    // Filter content
    Optional<String> filtered = name.filter(n -> n.length() > 5);
    System.out.println("Filtered: " + filtered.orElse("Filtered out"));
  }

  static String getNullable() {
    return null; // simulates nullable data
  }
}
