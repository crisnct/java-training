package com.example.training;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.StampedLock;

public class ConcurrencyGoodies {

  public static void main(String[] args) throws Exception {
    LongAdder adder = new LongAdder();
    LongAccumulator max = new LongAccumulator(Math::max, Long.MIN_VALUE);
    StampedLock lock = new StampedLock();

    ExecutorService pool = Executors.newFixedThreadPool(4);
    Runnable task = () -> {
      adder.increment();
      max.accumulate(ThreadLocalRandom.current().nextLong(1000));
      long stamp1 = lock.readLock();
      long stamp2 = lock.readLock();
      try {
        /* read shared state */
      } finally {
        lock.unlockRead(stamp1);
        lock.unlockRead(stamp2);
      }
    };
    for (int i = 0; i < 1000; i++) {
      pool.submit(task);
    }
    pool.shutdown();
    pool.awaitTermination(1, TimeUnit.SECONDS);

    // Optimistic read -> fallback to readLock if invalid
    long s = lock.tryOptimisticRead();
    boolean valid = lock.validate(s);
    if (!valid) {
      s = lock.readLock();
      try { /* safe read */ } finally {
        lock.unlockRead(s);
      }
    }

    System.out.println("count=" + adder.sum() + ", max=" + max.get());
  }
}
