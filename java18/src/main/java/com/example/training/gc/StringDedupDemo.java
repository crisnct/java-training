package com.example.training.gc;

import java.util.ArrayList;
import java.util.List;
//@formatter:off
/**
 * Java 18 GC / runtime tweaks demo:
 *
 * String deduplication is about saving heap memory when many distinct Java String objects contain identical content.
 *
 * 1) String deduplication now supported by more collectors:
 *    - SerialGC
 *    - ParallelGC
 *    - ZGC
 *    (on top of G1 / Shenandoah)
 *
 * 2) G1 heap region size can go up to 512 MB.
 *
 * HOW TO RUN (examples):
 *
 *   # G1 with string deduplication (supported before, kept in 18)
 *   java -XX:+UseG1GC -XX:+UseStringDeduplication StringDedupDemo
 *
 *   # Serial GC with string deduplication (extended support in 18)
 *   java -XX:+UseSerialGC -XX:+UseStringDeduplication StringDedupDemo
 *
 *   # Parallel GC with string deduplication (extended support in 18)
 *   java -XX:+UseParallelGC -XX:+UseStringDeduplication StringDedupDemo
 *
 *   # ZGC with string deduplication (extended support in 18)
 *   java -XX:+UseZGC -XX:+UseStringDeduplication StringDedupDemo
 *
 *   # G1 with very large region size (new max 512m in 18):
 *   java -XX:+UseG1GC -XX:G1HeapRegionSize=512m StringDedupDemo
 *
 * Use a profiler / jcmd / jmap to compare heap usage with and without
 * -XX:+UseStringDeduplication or with different collectors.
 *
 * You can use jvisualvm to analyze heap memory
 */
//@formatter:on
public class StringDedupDemo {

  private static final int COUNT = 10_000_000;

  public static void main(String[] args) {
    List<String> strings = new ArrayList<>(COUNT);

    String base = "This is a fairly long sample string used to show string deduplication in the JVM.";

    // Create many distinct String objects with identical content.
    // Ideal for the GC's string deduplication to kick in.
    for (int i = 0; i < COUNT; i++) {
      strings.add(new String(base));
    }

    System.out.println("Created " + COUNT + " duplicate strings.");
    System.out.println("PID: " + ProcessHandle.current().pid());
    System.out.println("Attach a profiler or use jcmd/jmap to inspect heap.");

    waitForEnter();
  }

  private static void waitForEnter() {
    try {
      System.out.println("Press Enter to exit...");
      System.in.read();
    } catch (Exception ignored) {
    }
  }
}
