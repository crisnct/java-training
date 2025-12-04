package com.example.training;

/**
 * Run with different VM flags:
 *
 * Java 1.3:
 *  -XX:+UseSerialGC -Xlog:gc    -> Single-threaded GC (all phases), Stop-the-world pauses for the entire duration.
 * Java 1.4:
 *  -XX:+UseParallelGC -Xlog:gc  -> Stop-the-world GC, but parallelized across multiple threads
 *  -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -Xlog:gc -> {
 *    Does most of the marking concurrently (with the application running).
 *    Fewer, shorter pauses than Serial/Parallel.
 *    Historically the best low-latency GC before G1.
 *    Multi-threaded marking.
 *  }
 * Java 1.7:
 *  -XX:+UseG1GC -Xlog:gc        -> {
 *      Splits the heap into regions
 *      Performs incremental, region-based collections.
 *      Tries to avoid full GC entirely; in Java 10, full GC became parallel.
 *      Focused on low pause times.
 *      Good for microservices
 *  }
 * Java 11:
 *  -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -> {
 *    Epsilon GC: no-op garbage collector useful for short-lived, performance tests
 *  }
 *  -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -> {
 *    Available only for Linux x64;
 *    A scalable low-latency GC with sub-millisecond pause times, even on large heaps (multi-GB).
 *  }
 */
public class GcWorkload {

  public static void main(String[] args) throws InterruptedException {
    for (int round = 0; round < 100; round++) {
      byte[][] garbage = new byte[10_000][];
      for (int i = 0; i < garbage.length; i++) {
        garbage[i] = new byte[1024]; // 1 KB objects
      }
      //System.out.println("Round " + round + " completed.");
    }

    System.out.println("Finished allocation workload.");

    System.out.println("Starting allocation test...");

    //it's expected to get OutOfMemoryError: Java heap space if you use:
    //-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC
    for (int i = 0; i < 50_000_000; i++) {
      // Allocate a bunch of short-lived objects
      byte[] data = new byte[1024]; // 1 KB
      blackhole(data);
    }

    System.out.println("Finished.");
  }

  // Prevents JIT from optimizing away the allocation loop
  private static void blackhole(Object obj) {
    // no-op
  }

}
