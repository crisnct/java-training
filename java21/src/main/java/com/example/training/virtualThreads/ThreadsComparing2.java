package com.example.training.virtualThreads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
//@formatter:off
/**
 * f the task is CPU-bound (heavy, in-memory computation with little or no I/O),
 * prefer platform threads with a bounded pool sized to your cores. Virtual threads don’t add throughput for pure
 * CPU work; they can even add a bit of overhead.
 * Practical rules:
 * CPU-bound ⇒ Executors.newFixedThreadPool(min(cores, N)) or ForkJoinPool.commonPool().
 * I/O-bound (even multi-minute) ⇒ virtual threads.
 * If you want a single “one executor” model, you can still use virtual threads but gate concurrency to ≈ number of cores.
 */
//@formatter:on
public class ThreadsComparing2 {

  public static void main(String[] args) throws Exception {
    int cores = Runtime.getRuntime().availableProcessors();
    int tasks = 100_000;

    // Option A: bounded PLATFORM pool — best for CPU-bound
    runCpuWork("platform", Executors.newFixedThreadPool(cores), tasks);

    // Option B: virtual threads + semaphore gate — works, but not faster
    Semaphore gate = new Semaphore(cores);
    ExecutorService vexec = Executors.newVirtualThreadPerTaskExecutor();
    runCpuWork("virtual+gate", r -> vexec.submit(() -> {
      gate.acquireUninterruptibly();
      try {
        r.run();
      } finally {
        gate.release();
      }
    }), tasks);
    vexec.shutdown();
  }

  private static void runCpuWork(String label, Executor executor, int tasks) throws Exception {
    long t0 = System.nanoTime();
    CountDownLatch latch = new CountDownLatch(tasks);
    AtomicLong sink = new AtomicLong();

    for (int i = 0; i < tasks; i++) {
      executor.execute(() -> {
        sink.addAndGet(spinFib(28));
        latch.countDown();
      });
    }
    latch.await();
    long ms = (System.nanoTime() - t0) / 1_000_000;
    System.out.println(label + " took " + ms + " ms, checksum=" + sink.get());
    if (executor instanceof ExecutorService es) {
      es.shutdown();
    }
  }

  // CPU-bound toy
  private static long spinFib(int n) {
    long a = 0, b = 1;
    for (int i = 0; i < n; i++) {
      long t = a + b;
      a = b;
      b = t;
    }
    return a;
  }
}
