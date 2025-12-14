package com.example.training;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReentrantLock;

//@formatter:off
/**
 * NonPinningSyncDemo
 *
 * Purpose:
 *   Demonstrate JEP 491 (Java 24): virtual threads no longer pin carrier threads
 *   when they park (e.g., Thread.sleep) inside a synchronized block.
 *
 * What it measures:
 *   1) synchronized + sleep    — used to pin before JEP 491
 *   2) ReentrantLock + sleep   — baseline that never pinned
 *   3) plain sleep             — best-case baseline
 *
 * How to read results:
 *   On Java 24, (1) should be close to (2) and (3).
 *   On older JDKs, (1) is typically much slower due to pinning.
 *
 * Tips:
 *   - No contention: each task uses its own monitor/lock; we measure pinning, not locking.
 *   - Tweak 'tasks', 'sleepsPerTask', 'sleepMillis' to amplify differences.
 *   - Optionally run with: -Djdk.virtualThreadScheduler.parallelism=4
 */
//@formatter:on
public class NonPinningSyncDemo {

  public static void main(String[] args) {
    final int tasks = 20_000;
    final int sleepsPerTask = 3;
    final int sleepMillis = 10;

    System.setProperty("jdk.virtualThreadScheduler.parallelism", "4");

    System.out.println("JDK: " + System.getProperty("java.version"));
    System.out.printf("tasks=%d, sleepsPerTask=%d, sleepMillis=%d%n", tasks, sleepsPerTask, sleepMillis);

    long tSync = run(tasks, vtSynchronizedSleeper(sleepsPerTask, sleepMillis));
    System.out.println("synchronized + sleep (ms): " + tSync);

    long tLock = run(tasks, vtReentrantLockSleeper(sleepsPerTask, sleepMillis));
    System.out.println("ReentrantLock + sleep (ms): " + tLock);

    long tPlain = run(tasks, vtPlainSleeper(sleepsPerTask, sleepMillis));
    System.out.println("plain sleep (ms): " + tPlain);

    System.out.printf("%nRatio sync/lock = %.2f, sync/plain = %.2f%n", tSync / (double) tLock, tSync / (double) tPlain);
  }

  private static long run(int tasks, Runnable task) {
    Instant start = Instant.now();
    try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
      List<Runnable> submissions = new ArrayList<>(tasks);
      for (int i = 0; i < tasks; i++) {
        submissions.add(task);
      }
      submissions.forEach(t -> {
        try {
          exec.submit(t);
        } catch (RejectedExecutionException ignored) {
        }
      });
    }
    return Duration.between(start, Instant.now()).toMillis();
  }

  private static Runnable vtSynchronizedSleeper(int repeats, int sleepMs) {
    return () -> {
      final Object localMonitor = new Object();
      for (int i = 0; i < repeats; i++) {
        synchronized (localMonitor) {
          sleep(sleepMs);
        }
      }
    };
  }

  private static Runnable vtReentrantLockSleeper(int repeats, int sleepMs) {
    return () -> {
      final ReentrantLock lock = new ReentrantLock();
      for (int i = 0; i < repeats; i++) {
        lock.lock();
        try {
          sleep(sleepMs);
        } finally {
          lock.unlock();
        }
      }
    };
  }

  private static Runnable vtPlainSleeper(int repeats, int sleepMs) {
    return () -> {
      for (int i = 0; i < repeats; i++) {
        sleep(sleepMs);
      }
    };
  }

  private static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }
}
