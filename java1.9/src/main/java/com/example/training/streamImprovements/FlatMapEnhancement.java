package com.example.training.streamImprovements;

import java.util.stream.IntStream;

public class FlatMapEnhancement {

  /**
   * For n = 1 → IntStream.range(0, 1) → 0
   * For n = 2 → IntStream.range(0, 2) → 0, 1
   * For n = 3 → IntStream.range(0, 3) → 0, 1, 2
   */
  public static void main(String[] args) {
    IntStream.of(1, 2, 3)
        .flatMap(n -> IntStream.range(0, n))
        .forEach(System.out::println);
  }

}
