
package com.example.training.jfrAndAOT;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

//@formatter:off
/**
 * Demonstrates Java 25's JFR Enhancements (JEP 468, 471, etc.):
 *  - Cooperative Sampling: lower pause overhead, synchronized with JVM safepoints.
 *  - Method Timing & Tracing: precise nanosecond timestamps for method activity.
 *
 * Real-world use:
 *  Profiles a mix of CPU- and I/O-bound tasks to generate a compact .jfr
 *  file for analysis in JDK Mission Control (JMC).
 */
//@formatter:on
public class JfrEnhancedDemo {

  static void main() throws Exception {
    Path jfrFile = Path.of("service-profile.jfr");

    try (Recording recording = new Recording()) {
      // Enable execution sampling with a 10ms period (correct syntax for JDK 25)
      recording.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.MethodSample").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.ThreadSleep");
      recording.enable("jdk.G1HeapSummary").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.ObjectAllocationSample").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.ObjectAllocationInNewTLAB");
      recording.enable("jdk.ObjectAllocationOutsideTLAB");
      recording.enable("jdk.GarbageCollection").withPeriod(Duration.ofSeconds(1));
      recording.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));

      System.out.println("> JFR start recording...");
      recording.start();
      simulateServiceWorkload();
      System.out.println("> JFR stop recording");
      recording.stop();
      System.out.println("> Writing to file " + jfrFile.toAbsolutePath());
      recording.dump(jfrFile);
    }

    System.out.println("✅ JFR recording saved");
    analyzeTopMethods(jfrFile);
  }

  // Simulated workload
  private static void simulateServiceWorkload() {
    List<Entry> entries = new ArrayList<>();
    for (int i = 0; i < 5_000; i++) {
      for (int j = 0; j < 5_000; j++) {
        entries.add(new Entry(j, Math.random() * 1000, "C" + j));
      }
      computeHeavyTask();
      if (i % 10 == 0) {
        ioBoundTask();
      }
      if (i % 1000 == 0) {
        entries.clear();
        System.gc();
      }
    }
  }

  record Entry(int id, double balance, String name) {

  }

  private static void computeHeavyTask() {
    double result = 0;
    for (int i = 0; i < 10_000; i++) {
      result += Math.sin(i) * Math.cos(i / 2.0);
    }
    if (result == 42) {
      System.out.println("impossible");
    }
  }

  private static void ioBoundTask() {
    try {
      Thread.sleep(ThreadLocalRandom.current().nextInt(2, 6)); // simulate short I/O
    } catch (InterruptedException ignored) {
    }
  }

  private static void analyzeTopMethods(Path jfrFile) throws Exception {
    List<RecordedEvent> events = RecordingFile.readAllEvents(jfrFile);

    // --- CPU samples ---
    long computeSamples = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.ExecutionSample"))
        .filter(e -> e.toString().contains("computeHeavyTask"))
        .count();

    long ioSamples = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.ExecutionSample"))
        .filter(e -> e.toString().contains("ioBoundTask"))
        .count();

    long gcCount = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.GarbageCollection"))
        .count();

    // --- Heap metrics from G1HeapSummary ---
    long edenUsed = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.G1HeapSummary"))
        .mapToLong(e -> safeLong(e, "edenUsedSize"))
        .max().orElse(0L);

    long survivorUsed = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.G1HeapSummary"))
        .mapToLong(e -> safeLong(e, "survivorUsedSize"))
        .max().orElse(0L);

    long oldGenUsed = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.G1HeapSummary"))
        .mapToLong(e -> safeLong(e, "oldGenUsedSize"))
        .max().orElse(0L);

    long edenTotal = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.G1HeapSummary"))
        .mapToLong(e -> safeLong(e, "edenTotalSize"))
        .max().orElse(0L);

    long totalUsed = edenUsed + survivorUsed + oldGenUsed;
    double edenUtil = edenTotal > 0 ? (edenUsed * 100.0 / edenTotal) : 0.0;

    System.out.println("\n=== Profiling Summary (JFR) ===");
    System.out.printf("Samples hitting computeHeavyTask(): %,d%n", computeSamples);
    System.out.printf("Samples hitting ioBoundTask(): %,d%n", ioSamples);
    System.out.printf("Garbage Collection events: %,d%n", gcCount);
    System.out.println("--- Heap breakdown ---");
    System.out.printf("Eden used: %.2f MB%n", edenUsed / 1_000_000.0);
    System.out.printf("Survivor used: %.2f MB%n", survivorUsed / 1_000_000.0);
    System.out.printf("OldGen used: %.2f MB%n", oldGenUsed / 1_000_000.0);
    System.out.printf("Total heap used: %.2f MB%n", totalUsed / 1_000_000.0);
    System.out.printf("Eden utilization: %.2f%%%n", edenUtil);
  }


  // Helper for safe field access
  private static long safeLong(RecordedEvent e, String field) {
    try {
      return e.getLong(field);
    } catch (Exception ignored) {
      return 0L;
    }
  }


}
