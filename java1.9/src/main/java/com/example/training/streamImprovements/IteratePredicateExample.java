package com.example.training.streamImprovements;

import java.util.stream.Stream;

public class IteratePredicateExample {

  public static void main(String[] args) {
    Stream
        .iterate(1, n -> n <= 10, n -> n + 1)
        .forEach(System.out::println);
  }
}
