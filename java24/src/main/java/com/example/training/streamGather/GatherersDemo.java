package com.example.training.streamGather;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//@formatter:off
/**
 * Demonstrates every public operation in {@code java.util.stream.Gatherers} (Java 24).
 *
 * What you see:
 *  - windowFixed(k): groups elements into non-overlapping windows of size k (last may be smaller).
 *  - windowSliding(k): overlapping windows of size k, advanced by one element.
 *  - scan(initial, op): emits each prefix accumulation (running totals/aggregates).
 *  - fold(initial, op): ordered reduction that emits exactly one element downstream.
 *  - mapConcurrent(max, mapper): concurrent mapping on virtual threads, preserving encounter order.
 *
 * Quick run:
 *   javac GatherersShowcase.java && java GatherersShowcase
 */
//@formatter:on
public class GatherersDemo {

  public static void main(String[] args) {
    windowFixedDemo();
    windowSlidingDemo();
    scanDemo();
    foldDemo();
    mapConcurrentDemo();
  }

  private static void windowFixedDemo() {
    List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> fixed = nums.stream()
        .gather(Gatherers.windowFixed(3))
        .toList();
    System.out.println("windowFixed(3): " + fixed);
  }

  private static void windowSlidingDemo() {
    List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8);
    List<List<Integer>> sliding = nums.stream()
        .gather(Gatherers.windowSliding(4))
        .toList();
    System.out.println("windowSliding(4): " + sliding);
  }

  private static void mapConcurrentDemo() {
    List<Integer> nums = IntStream.rangeClosed(1, 10_000)
        .parallel().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    //standard way
    Instant startTime = Instant.now();
    List<Integer> doubled1 = nums.parallelStream()
        .map(GatherersDemo::slowDouble)
        .toList();
    System.out.println("Duration 1 = " + Duration.between(startTime, Instant.now()).toMillis());

    //using gather
    startTime = Instant.now();
    List<Integer> doubled2 = nums.stream()
        .gather(Gatherers.mapConcurrent(256, GatherersDemo::slowDouble))
        .toList();
    System.out.println("Duration 2 = " + Duration.between(startTime, Instant.now()).toMillis());

    System.out.println("mapConcurrent(*2): " + doubled1.size());
    System.out.println("mapConcurrent(*2): " + doubled2.size());
  }

  private static void scanDemo() {
    List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8);
    List<Integer> prefixSums = nums.stream()
        .gather(Gatherers.scan(() -> 0, Integer::sum))
        .toList();
    System.out.println("scan sum: " + prefixSums);

    List<String> segments = List.of("home", "products", "laptops", "ultrabooks");
    // After each segment, emit the current full path
    List<String> paths = segments.stream()
        .gather(Gatherers.scan(() -> "", (acc, s) -> acc.isEmpty() ? s : acc + "/" + s))
        .toList();
    System.out.println("scan path:" + paths); // [home, home/products, home/products/laptops, home/products/laptops/ultrabooks]
  }

  private static void foldDemo() {
    // An intermediate stream step that reduces the entire upstream into a single value,
    // then emits exactly one element downstream. Think “reduce but as a mid-pipeline operator.
    String joined = Stream.of("a", "b", "c", "d")
        .gather(Gatherers.fold(() -> "initial", (acc, s) -> acc.isEmpty() ? s : acc + "-" + s))
        .findFirst()
        .orElse("<empty>");
    System.out.println("fold join: " + joined);

    List<String> joined2 = Stream.of("a", "b", "c", "d")
        .gather(Gatherers.fold(() -> "initial",
            (accumulator, currValue) -> accumulator.isEmpty() ? currValue : accumulator + "-" + currValue))
        .toList();
    System.out.println("fold join2: " + joined2);

    int sum = Stream.of(1, 2, 3, 4)
        .gather(Gatherers.fold(() -> 0, Integer::sum))
        .findFirst().orElse(0);
    System.out.println("sum=" + sum);
  }

  private static int slowDouble(int n) {
    try {
      Thread.sleep(10); // pretend HTTP/DB latency
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    long spins = ThreadLocalRandom.current().nextLong(60_000, 120_000);
    while (spins-- > 0) { /* tiny busy-wait to simulate work */ }
    return n * 2;
  }
}
