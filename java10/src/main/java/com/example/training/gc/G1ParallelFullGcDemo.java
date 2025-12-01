package com.example.training.gc;

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
public class G1ParallelFullGcDemo {

  // ~10 MB per array
  private static final int CHUNK_SIZE = 10 * 1024 * 1024;

  public static void main(String[] args) {
    List<byte[]> allocations = new ArrayList<>();

    // 1) Fill the heap with large arrays to create pressure
    for (int i = 0; i < 30; i++) {
      allocations.add(new byte[CHUNK_SIZE]);
      // Occasionally drop a reference to create some garbage
      if (i % 5 == 0) {
        allocations.set(0, null);
      }
    }

    //System.out.println("Requesting explicit full GC...");
    System.gc(); // may trigger a Full GC under G1

    // 2) Allocate more, to keep the GC busy after the full GC
    for (int i = 0; i < 10; i++) {
      allocations.add(new byte[CHUNK_SIZE]);
    }

    System.out.println("Demo finished.");
  }
}
