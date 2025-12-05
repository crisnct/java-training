package com.example.training;

// @formatter:off
/**
 *  Java 14:
 *     -XX:+UseG1GC -XX:+UseNUMA -Xlog:gc+heap=debug -Xmx4g -> JEP 345 (NUMA-Aware Memory Allocation for G1) in Java 14 is a JVM-level optimization,
 *     not something you can express directly in Java syntax. It improves performance on multi-socket NUMA systems (where each CPU socket has its own local memory bank) by
 *     making the G1 garbage collector allocate and rebalance heap regions closer to the CPU threads that use them.
 */
// @formatter:on
public class NumaAwareG1Demo {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();

    // Allocate and touch a large amount of memory
    int size = 500_000_000;
    byte[] data = new byte[size];
    for (int i = 0; i < size; i++) {
      data[i] = (byte) (i % 100);
    }

    long elapsed = System.currentTimeMillis() - start;
    System.out.println("Work completed in " + elapsed + " ms");
  }
}
