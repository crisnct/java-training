package com.example.training.streams;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ParallelStreamDemo2 {

  public static void main(String[] args) {

    List<String> urls = Arrays.asList(
        "https://example.com/a",
        "https://example.com/b",
        "https://example.com/c",
        "https://example.com/d"
    );

    long start = System.currentTimeMillis();

    // BAD: blocking inside parallel stream
    List<String> results = urls.parallelStream()
        .map(url -> {
          sleep(1000); // simulating slow I/O
          return "Response from " + url;
        })
        .collect(Collectors.toList());

    long time = System.currentTimeMillis() - start;

    System.out.println(results);
    System.out.println("Parallel stream BLOCKING time: " + time + " ms");
  }

  private static void sleep(long ms) {
    try {
      TimeUnit.MILLISECONDS.sleep(ms);
    } catch (InterruptedException ignored) {
    }
  }
}
