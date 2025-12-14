package com.example.training;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * G1LateBarrierDemo (Java 17+)
 * <p>
 * Goal: stress many *reference writes* (triggers G1 pre/post barriers). Compare JDK 23 vs JDK 24 (both with G1). JEP 475 should reduce C2 overhead,
 * so JDK 24 usually shows higher ops/sec for the same code.
 * <p>
 * How it works: - Preallocates a pool of Node objects to avoid allocation effects. - Performs millions of reference writes into an Object[] table and
 * Node.next fields. - Captures ops/sec. The math is crude but good enough to see the delta.
 * <p>
 * Run (set a fixed heap to stabilize GC): # JDK 23 (baseline) java -XX:+UseG1GC -Xms512m -Xmx512m G1LateBarrierDemo # JDK 24 (JEP 475) java
 * -XX:+UseG1GC -Xms512m -Xmx512m G1LateBarrierDemo
 * <p>
 * Optional logs to confirm G1: -Xlog:gc*,safepoint,class+load=info
 */
public class G1LateBarrierDemo {

  static final class Node {

    volatile Object next; // reference write target (barrier)
  }

  public static void main(String[] args) {
    final int tableSize = 2_000_000;     // object reference table
    final int poolSize = 2_000_000;     // pool of Node objects
    final int rounds = 5;             // repetitions to smooth out noise
    final int writesPerRound = 20_000_000;

    System.out.println("java.version = " + System.getProperty("java.version"));
    System.out.println("tableSize=" + tableSize + " poolSize=" + poolSize + " writes/round=" + writesPerRound);

    // Preallocate objects so the benchmark measures *writes*, not allocation
    Object[] table = new Object[tableSize];
    Node[] pool = new Node[poolSize];
    for (int i = 0; i < pool.length; i++) {
      pool[i] = new Node();
    }


    long best = Long.MAX_VALUE;
    long worst = Long.MIN_VALUE;
    long sum = 0;

    for (int r = 1; r <= rounds; r++) {
      long nanos = runRound(table, pool, writesPerRound);
      long ms = Duration.ofNanos(nanos).toMillis();
      System.out.println("Round " + r + ": " + ms + " ms");
      best = Math.min(best, ms);
      worst = Math.max(worst, ms);
      sum += ms;
    }

    double avg = sum / (double) rounds;
    double opsPerSec = (writesPerRound / (avg / 1000.0));
    System.out.printf("avg=%.1f ms, best=%d ms, worst=%d ms, ~ops/sec=%.0f%n", avg, best, worst, opsPerSec);
  }

  private static long runRound(Object[] table, Node[] pool, int writes) {
    Random rnd = new Random(42);
    Instant t0 = Instant.now();

    final int maskT = table.length - 1;
    final int maskP = pool.length - 1;

    // Phase 1: write references into the big Object[] table
    for (int i = 0; i < writes / 2; i++) {
      int idxT = rnd.nextInt() & maskT;
      int idxP = rnd.nextInt() & maskP;
      table[idxT] = pool[idxP];              // reference write -> G1 barrier
    }

    // Phase 2: write Node.next fields (another barrier hotspot)
    for (int i = 0; i < writes / 2; i++) {
      int from = rnd.nextInt() & maskP;
      int to = rnd.nextInt() & maskP;
      pool[from].next = pool[to];            // reference write -> G1 barrier
    }

    return Duration.between(t0, Instant.now()).toNanos();
  }
}
