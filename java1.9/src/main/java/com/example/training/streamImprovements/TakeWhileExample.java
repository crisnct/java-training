package com.example.training.streamImprovements;

import java.util.stream.Stream;

public class TakeWhileExample {

  public static void main(String[] args) {
    Stream<Integer> numbers = Stream.of(1, 2, 3, 7, 1, 2);
    numbers
        .takeWhile(n -> n < 5)
        .forEach(System.out::println);
  }
}
