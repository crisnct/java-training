
package com.example.training.jfrAndAOT;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
//@formatter:off
/**
 * Start the app with VM option --aot
 *
 * IMPORTANT!!!
 * The --aot flag is part of the AOT Ergonomics JEP (internal to Oracle JDK and Graal builds starting in 25),
 * but not shipped in community OpenJDK / Temurin distributions — at least not yet.
 *
 * Demonstrates Java 25's Ahead-of-Time Command-Line Ergonomics & Method Profiling.
 *
 * 1️⃣ JVM now automatically tunes tiered compilation & code cache sizing when using --aot
 *    (no need to manually pass complex -XX options).
 *
 * 2️⃣ Method Profiling: via JFR, the JVM exposes detailed per-method execution stats
 *    usable to decide what code should be AOT-compiled.
 *
 * Real-world scenario:
 *  We simulate a microservice with hot and cold methods, record JFR method stats,
 *  and show how you'd use them to feed an AOT build pipeline.
 */
//@formatter:on
public class AotErgonomicsDemo {

  static void main() throws Exception {
    System.out.println("Starting demo — running simulated workload...");
    runWithMethodProfiling();
    System.out.println("Profiling complete. Generated method stats under: method-profile.jfr");
  }

  // Simulate a service workload and record JFR method-level data
  private static void runWithMethodProfiling() throws Exception {
    Path jfrFile = Path.of("method-profile.jfr");
    try (Recording recording = new Recording()) {
      // JVM now cooperates with AOT ergonomics — enabling method profiling automatically
      recording.enable("jdk.MethodSample").withPeriod(Duration.ofMillis(5));
      recording.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(5));
      recording.enable("jdk.G1HeapSummary").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.ObjectAllocationSample").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.ObjectAllocationInNewTLAB");
      recording.enable("jdk.ObjectAllocationOutsideTLAB");
      recording.enable("jdk.GarbageCollection").withPeriod(Duration.ofSeconds(1));
      recording.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));

      recording.start();

      // Simulate workload for ~5 seconds
      long end = System.currentTimeMillis() + 5000;
      while (System.currentTimeMillis() < end) {
        heavyComputation();
        if (System.currentTimeMillis() % 1000 < 20) {
          rarelyUsed();
        }
      }

      // Give JFR time to flush samples
      Thread.sleep(500);
      recording.stop();
      recording.dump(jfrFile);
    }

    analyzeHotMethods(jfrFile);
  }

  private static void heavyComputation() {
    double res = 0;
    for (int i = 0; i < 1000; i++) {
      res += Math.sin(i) * Math.cos(i / 2.0);
    }
    if (res == 42) {
      System.out.println("impossible");
    }
  }

  private static void rarelyUsed() {
    try {
      Thread.sleep(50);
    } catch (InterruptedException ignored) {
    }
  }

  // Analyze which methods are most sampled — guides AOT targets
  private static void analyzeHotMethods(Path jfrFile) throws Exception {
    List<RecordedEvent> events = RecordingFile.readAllEvents(jfrFile);
    long heavy = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.ExecutionSample"))
        .filter(e -> e.toString().contains("heavyComputation"))
        .count();
    long rare = events.stream()
        .filter(e -> e.getEventType().getName().equals("jdk.ExecutionSample"))
        .filter(e -> e.toString().contains("rarelyUsed"))
        .count();

    System.out.println("\n=== Hot Method Analysis ===");
    System.out.printf("heavyComputation() samples: %,d%n", heavy);
    System.out.printf("rarelyUsed() samples: %,d%n", rare);
    if (heavy > rare * 5) {
      System.out.println("→ heavyComputation() is a clear AOT candidate.");
    } else {
      System.out.println("→ no clear hotspot detected.");
    }
  }
}
