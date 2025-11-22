package com.example.training;

public class JitDemo {

  public static void main(String[] args) {
    long start, end;

    // Prima rulare - JVM interpretează bytecode-ul
    start = System.currentTimeMillis();
    runComputation();
    end = System.currentTimeMillis();
    System.out.println("Prima rulare (interpretată): " + (end - start) + " ms");

    // A doua rulare - JIT începe să observe metodele "fierbinți"
    start = System.currentTimeMillis();
    runComputation();
    end = System.currentTimeMillis();
    System.out.println("A doua rulare (JIT încălzit parțial): " + (end - start) + " ms");

    // A treia rulare - JIT a compilat nativ metoda
    start = System.currentTimeMillis();
    runComputation();
    end = System.currentTimeMillis();
    System.out.println("A treia rulare (compilată JIT): " + (end - start) + " ms");

  }

  private static void runComputation() {
    long sum = 0;
    for (int i = 0; i < 100000000; i++) {
      sum += i % 3;
    }
    if (sum == -1) { // doar pentru a preveni optimizarea inutilă
      System.out.println(sum);
    }
  }
}
