package com.example.training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceDemo {

  public static final int OBJECTS = 2000000;

  public static void main(String[] args) {
    long appStart = System.currentTimeMillis();
    // Small warmup so HotSpot can JIT compile hot methods.
    warmUpJit();
    long afterWarmup = System.currentTimeMillis();
    System.out.println("Startup + warmup time: " + (afterWarmup - appStart) + " ms");

    runBenchmark("Primitive sum (tight loop)", new Workload() {
      public void run() {
        primitiveSum();
      }
    });
    runBenchmark("Autoboxing sum (more allocations / GC pressure)", new Workload() {
      public void run() {
        boxedSum();
      }
    });
    runBenchmark("Short-lived object allocation (GC test)", new Workload() {
      public void run() {
        allocateShortLivedObjects();
      }
    });
    runBenchmark("Collections sort and map lookup (core libs)", new Workload() {
      public void run() {
        collectionsWorkload();
      }
    });
  }

  // Simple interface instead of lambdas (Java 6 style)
  private interface Workload {

    void run();
  }

  private static void runBenchmark(String name, Workload workload) {
    // Warmup for this specific workload
    for (int i = 0; i < 5; i++) {
      workload.run();
    }

    long start = System.currentTimeMillis();
    for (int i = 0; i < 20; i++) {
      workload.run();
    }
    long end = System.currentTimeMillis();

    System.out.println(name + " took: " + (end - start) + " ms");
  }

  // -----------------------------
  // 1) HotSpot JIT optimization
  // -----------------------------
  private static void primitiveSum() {
    long sum = 0L;
    // Tight numeric loop – HotSpot gets very good at this
    for (int i = 0; i < OBJECTS; i++) {
      sum += i;
    }
    // Prevent JVM from optimizing away the loop
    if (sum == -1) {
      System.out.println("Impossible");
    }
  }

  // Same logic but with autoboxing to create more pressure
  private static void boxedSum() {
    Long sum = 0L; // boxed
    for (int i = 0; i < OBJECTS; i++) {
      sum += i;   // each "+=" can create temporary Long objects
    }
    if (sum.longValue() == -1L) {
      System.out.println("Impossible");
    }
  }

  // -----------------------------
  // 2) GC optimizations example
  // -----------------------------
  private static void allocateShortLivedObjects() {
    for (int i = 0; i < OBJECTS; i++) {
      // Many short-lived allocations; in Java 6, young-gen GC is improved.
      Dummy d = new Dummy(i, "name" + i);
      // Objects die very quickly; they should be collected cheaply.
      if (d.id == -1) {
        System.out.println("Never happens");
      }
    }
  }

  private static class Dummy {

    int id;
    String name;

    Dummy(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  // -----------------------------
  // 3) Core library refinements
  // -----------------------------
  private static void collectionsWorkload() {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < OBJECTS; i++) {
      list.add("item-" + i);
    }

    // Sorting – Arrays.sort / Collections.sort saw constant refinements
    Collections.sort(list);

    // HashMap – common operations got better tuned over JDK releases
    Map<String, Integer> map = new HashMap<String, Integer>();
    for (int i = 0; i < list.size(); i++) {
      map.put(list.get(i), Integer.valueOf(i));
    }

    int found = 0;
    for (int i = 0; i < OBJECTS; i++) {
      String key = "item-" + (i * 2);
      Integer value = map.get(key);
      if (value != null) {
        found += value.intValue();
      }
    }

    if (found == -1) {
      System.out.println("Never happens");
    }
  }

  // -----------------------------
  // 4) Simple JIT warmup
  // -----------------------------
  private static void warmUpJit() {
    for (int i = 0; i < 10; i++) {
      primitiveSum();
      boxedSum();
      allocateShortLivedObjects();
      collectionsWorkload();
    }
  }

}
