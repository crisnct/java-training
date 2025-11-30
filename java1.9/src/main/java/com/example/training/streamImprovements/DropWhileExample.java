package com.example.training.streamImprovements;

import java.util.stream.Stream;

public class DropWhileExample {

  public static void main(String[] args) {
    Stream<Integer> numbers = Stream.of(1, 2, 3, 7, 1, 2);
    numbers
        .dropWhile(n -> n < 5)
        .forEach(System.out::println);
  }
}
