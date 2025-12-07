package com.example.training;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

//@formatter:off
/**
 * Demonstrates the Java 20 G1 MXBean named "G1 Concurrent GC".
 *
 * Run with something like:
 *   java -Xms256m -Xmx256m -XX:+UseG1GC com.example.training.G1ConcurrentGcStatsDemo
 *
 * Let it run for a while so that G1 can start concurrent mark cycles.
 */
//@formatter:on
public class G1ConcurrentGcStatsDemo {

  private static final List<byte[]> holder = new ArrayList<>();

  public static void main(String[] args) throws InterruptedException {
    List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
    GarbageCollectorMXBean g1ConcurrentGcBean = findBean(gcBeans, "G1 Concurrent GC");

    if (g1ConcurrentGcBean == null) {
      System.out.println("G1 Concurrent GC MXBean not found. " +
          "Are you running with G1 on Java 20+?");
      return;
    }

    System.out.println("Found bean: " + g1ConcurrentGcBean.getName());

    for (int iteration = 0; iteration < 30; iteration++) {
      allocateSomeGarbage();

      long count = g1ConcurrentGcBean.getCollectionCount();
      long time = g1ConcurrentGcBean.getCollectionTime();

      System.out.printf("iter=%02d  G1 Concurrent GC: count=%d  time=%d ms%n",
          iteration, count, time);

      Thread.sleep(500L);
    }
  }

  private static void allocateSomeGarbage() {
    // Allocate and keep some objects alive so they can age into old gen
    for (int i = 0; i < 200; i++) {
      holder.add(new byte[1024 * 1024]); // 1 MB
    }
    // Drop some references so GC can reclaim at least part of them
    if (holder.size() > 1_000) {
      holder.subList(0, 500).clear();
    }
  }

  private static GarbageCollectorMXBean findBean(List<GarbageCollectorMXBean> beans, String name) {
    for (GarbageCollectorMXBean bean : beans) {
      if (name.equals(bean.getName())) {
        return bean;
      }
    }
    return null;
  }
}
