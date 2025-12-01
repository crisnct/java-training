package com.example.training.gc;

/**
 *Run with different VM flags:
 *  -XX:+UseSerialGC -Xlog:gc    -> Single-threaded GC (all phases), Stop-the-world pauses for the entire duration.
 *  -XX:+UseParallelGC -Xlog:gc  -> Stop-the-world GC, but parallelized across multiple threads
 *  -XX:+UseG1GC -Xlog:gc        -> {
 *      Splits the heap into regions
 *      Performs incremental, region-based collections.
 *      Tries to avoid full GC entirely; in Java 10, full GC became parallel.
 *      Focused on low pause times.
 *      Good for microservices
 *  }
 *  -XX:+UseConcMarkSweepGC -Xlog:gc    -> very old {
 *    Does most of the marking concurrently (with the application running).
 *    Fewer, shorter pauses than Serial/Parallel.
 *    Historically the best low-latency GC before G1.
 *    Multi-threaded marking.
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
  }
}
