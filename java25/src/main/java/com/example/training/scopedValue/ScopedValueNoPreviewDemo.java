//@formatter:off
/**
 * Demonstrates Java 25's ScopedValue (final, JEP 481) with no preview features.
 *
 * ScopedValue lets you share immutable context safely across threads and methods
 * without the memory-leak problems of ThreadLocal.
 *
 * This example simulates concurrent tasks processing a batch of requests.
 * Each request has its own trace ID bound in a ScopedValue.
 */
//@formatter:on
package com.example.training.scopedValue;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScopedValueNoPreviewDemo {

  // Immutable contextual variable
  private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();

  static void main() throws Exception {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

      // Simulate multiple independent requests
      for (int i = 1; i <= 3; i++) {
        final String trace = "REQ-" + Instant.now().toEpochMilli() + "-" + i;
        executor.submit(() -> handleRequest(trace));
        Thread.sleep(100); // stagger requests slightly
      }

      Thread.sleep(1000); // wait for all to finish
    }
  }

  private static void handleRequest(String traceId) {
    // Bind ScopedValue to this logical scope
    ScopedValue.where(TRACE_ID, traceId).run(() -> {
      log("Start handling request");
      businessLogic();
      log("Finished handling request");
    });
  }

  private static void businessLogic() {
    log("Performing business logic...");
    try {
      Thread.sleep(200);
    } catch (InterruptedException ignored) {
    }
    log("Business logic done.");
  }

  private static void log(String msg) {
    String trace = TRACE_ID.isBound() ? TRACE_ID.get() : "NO_TRACE";
    System.out.printf("[%s] %s%n", trace, msg);
  }
}
