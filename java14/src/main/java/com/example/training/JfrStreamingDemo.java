package com.example.training;// File: JfrStreamingDemo.java

import java.time.Duration;
import jdk.jfr.consumer.RecordingStream;

/**
 * Use this argument for more logging
 * -Xlog:jfr+system=info
 */
public class JfrStreamingDemo {

  public static void main(String[] args) {
    try (RecordingStream stream = new RecordingStream()) {
      System.out.println("Start");
      // Enable the events and start streaming
      stream.enable("jdk.GarbageCollection").withPeriod(Duration.ofSeconds(1));
      stream.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));

      // Subscribe to garbage collection events
      stream.onEvent("jdk.GarbageCollection", event -> {
        String name = event.getString("name");
        long duration = event.getDuration().toMillis();
        System.out.println("GC: " + name + " took " + duration + " ms");
      });

      // Subscribe to CPU load events
      stream.onEvent("jdk.CPULoad", event -> {
        double jvm = event.getDouble("jvmUser");
        double system = event.getDouble("machineTotal");
        System.out.printf("CPU Load - JVM: %.2f%%, System: %.2f%%%n", jvm * 100, system * 100);
      });

      stream.startAsync();  // Start streaming asynchronously

      // Simulate workload to trigger GC events
      for (int i = 0; i < 10_000; i++) {
        byte[] block = new byte[5_000_000];
        block[0] = 1;
        Thread.sleep(100);
      }
      System.out.println("Finished");
    } catch (Exception e) {
      Thread.currentThread().interrupt();
    }
  }
}
