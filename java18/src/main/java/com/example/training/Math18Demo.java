package com.example.training;//@formatter:off
/**
 * Demonstrates Java 18 numeric improvements in Math:
 *
 * New methods:
 *  - Math.ceilDiv(...)
 *  - Math.ceilDivExact(...)
 *  - Math.ceilMod(...)
 *  - Math.divideExact(...)
 *  - Math.unsignedMultiplyHigh(...)
 *
 * These methods reduce boilerplate and make integer math safer and more consistent.
 */
//@formatter:on
public class Math18Demo {

  public static void main(String[] args) {

    // ===== Ceil division =====
    // Normal integer division: 7 / 3 = 2
    // Ceil division rounds up: 7 / 3 = 3
    System.out.println("ceilDiv(7, 3) = " + Math.ceilDiv(7, 3));

    // ===== Ceil modulo =====
    // Ensures non-negative modulo result, consistent with mathematical modulo
    System.out.println("ceilMod(7, 3) = " + Math.ceilMod(7, 3));

    // ===== Exact ceil division =====
    // Throws ArithmeticException if not exact
    try {
      System.out.println("ceilDivExact(6, 3) = " + Math.ceilDivExact(6, 3));
      System.out.println("ceilDivExact(7, 3) = " + Math.ceilDivExact(7, 3));  // throws
    } catch (ArithmeticException ex) {
      System.out.println("ceilDivExact(7, 3) failed: " + ex.getMessage());
    }

    // ===== divideExact =====
    // Throws ArithmeticException if result is not exact integer division
    try {
      System.out.println("divideExact(10, 5) = " + Math.divideExact(10, 5));
      System.out.println("divideExact(7, 3) = " + Math.divideExact(7, 3));    // throws
    } catch (ArithmeticException ex) {
      System.out.println("divideExact(7, 3) failed: " + ex.getMessage());
    }

    // ===== unsignedMultiplyHigh =====
    // Returns the top 64 bits of a 128-bit unsigned multiplication
    long a = 0xFFFF_FFFF_FFFF_FFFEL;   // large number
    long b = 123456789L;

    long res = Math.unsignedMultiplyHigh(a, b);
    System.out.println("unsignedMultiplyHigh(...) = " + res);

    // For comparison, if you were doing this manually before:
    // BigInteger big = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
    // long topBits = big.shiftRight(64).longValue();
    // Now it's one call with no allocation.
  }
}
