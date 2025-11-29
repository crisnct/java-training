package com.example.training.streams;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamDemo1 {

  public static void main(String[] args) {
    List<Integer> numbers = IntStream.rangeClosed(1, 20_000_000)
        .boxed()
        .collect(Collectors.toList());

    long start = System.currentTimeMillis();
    // CPU-heavy pure computation on parallel stream
    long count = numbers.parallelStream()
        .map(ParallelStreamDemo1::heavyCompute)              // pure computation
        .filter(n -> n % 3 == 0)      // pure, no I/O
        .count();                     // safe terminal op
    long time = System.currentTimeMillis() - start;

    System.out.println("Count = " + count);
    System.out.println("Parallel stream time: " + time + " ms");

    start = System.currentTimeMillis();
    count = numbers.stream()
        .map(ParallelStreamDemo1::heavyCompute)              // pure computation
        .filter(n -> n % 3 == 0)      // pure, no I/O
        .count();                     // safe terminal op
    time = System.currentTimeMillis() - start;
    System.out.println("Normal stream time: " + time + " ms");
  }

  private static int heavyCompute(int n) {
    // artificial CPU work
    int r = n;
    for (int i = 0; i < 100; i++) {
      r = (r * 31) ^ (r >>> 3);
    }
    return r;
  }
}
