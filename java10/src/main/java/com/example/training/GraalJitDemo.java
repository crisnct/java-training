package com.example.training;

/**
 * Graal as an experimental, Java-based JIT compiler inside the JDK
 * Can be enabled with VM flags:
 * -XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler
 */
public class GraalJitDemo {

  private static final int OUTER_LOOPS = 20_000;
  private static final int INNER_LOOPS = 5_000;

  public static void main(String[] args) {
    System.out.println("Java version: " + System.getProperty("java.version"));
    System.out.println("Warm-up and benchmark starting...");

    long start = System.nanoTime();
    long result = 0L;
    for (int i = 0; i < OUTER_LOOPS; i++) {
      result += hotMethod(i);
    }
    long end = System.nanoTime();

    double millis = (end - start) / 1_000_000.0;
    System.out.println("Result = " + result);
    System.out.println("Total time = " + millis + " ms");
  }

  // This method should get hot and trigger JIT compilation
  private static long hotMethod(int seed) {
    long acc = seed;
    for (int i = 0; i < INNER_LOOPS; i++) {
      acc ^= (acc << 1) ^ (acc >>> 3);
      acc += 0x9E3779B97F4A7C15L; // some “random” mixing
    }
    return acc;
  }
}
