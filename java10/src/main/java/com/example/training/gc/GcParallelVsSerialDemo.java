package com.example.training.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

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
public class GcParallelVsSerialDemo {

  private static final int CHUNK_SIZE = 10 * 1024 * 1024; // ~10 MB per array

  private static final int ROUNDS = 10;

  private static class GcStats {
    long collections;
    long timeMillis;
  }

  public static void main(String[] args) {
    printGcInfo();

    GcStats before = readGcStats();
    long start = System.nanoTime();

    runAllocationWorkload();

    // Try to trigger a full GC
    System.out.println("Requesting explicit full GC...");
    System.gc();

    // Small extra allocations to keep pressure after GC
    postGcAllocations();

    long end = System.nanoTime();
    GcStats after = readGcStats();

    long elapsedMillis = (end - start) / 1_000_000;
    long gcCollections = after.collections - before.collections;
    long gcTimeMillis = after.timeMillis - before.timeMillis;

    System.out.println("--------------------------------------------------");
    System.out.println("Elapsed time (total):      " + elapsedMillis + " ms");
    System.out.println("GC collections (delta):    " + gcCollections);
    System.out.println("GC time (delta):           " + gcTimeMillis + " ms");
    System.out.println("Non-GC time (approx):      " + (elapsedMillis - gcTimeMillis) + " ms");
    System.out.println("--------------------------------------------------");
  }

  private static void runAllocationWorkload() {
    List<byte[]> allocations = new ArrayList<>();

    for (int round = 0; round < ROUNDS; round++) {
      System.out.println("Round " + round + " allocations...");
      for (int i = 0; i < 20; i++) {
        allocations.add(new byte[CHUNK_SIZE]);

        // occasionally drop some references to create garbage
        if (i % 5 == 0 && !allocations.isEmpty()) {
          allocations.set(0, null);
        }
      }
    }
  }

  private static void postGcAllocations() {
    List<byte[]> extra = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      extra.add(new byte[CHUNK_SIZE]);
      System.out.println("Post-GC allocation " + i);
    }
  }

  private static GcStats readGcStats() {
    long totalCollections = 0;
    long totalTime = 0;

    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      long c = gcBean.getCollectionCount();
      if (c != -1) {
        totalCollections += c;
      }
      long t = gcBean.getCollectionTime();
      if (t != -1) {
        totalTime += t;
      }
    }

    GcStats stats = new GcStats();
    stats.collections = totalCollections;
    stats.timeMillis = totalTime;
    return stats;
  }

  private static void printGcInfo() {
    System.out.println("=== Active Garbage Collectors ===");
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      System.out.println("GC Name: " + gcBean.getName() + " | Type: " + gcBean.getObjectName());
    }
    System.out.println("=================================");
  }
}
