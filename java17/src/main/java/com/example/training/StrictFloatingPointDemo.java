package com.example.training;

//@formatter:off
/**
 * Always-strict floating-point semantics
 * JEP 306 – Restore Always-Strict Floating-Point Semantics
 * Removes the old “relaxed” FP behavior from pre-1.2 days; now all FP expressions behave as if strictfp is applied.
 * Example that illustrates the “always-strict” floating-point behavior and the fact that strictfp no longer changes the result.
 * Before Java 17, on some older platforms (especially x87-based), a non-strictfp method could end up using extended precision
 * internally, so computeDefault and computeStrict might have given different results in edge cases.
 */
//@formatter:on
public class StrictFloatingPointDemo {

  public static void main(String[] args) {
    double base = 1.0e308;

    double nonStrictResult = computeDefault(base);
    double strictResult = computeStrict(base);

    System.out.println("default result = " + nonStrictResult);
    System.out.println("strict  result = " + strictResult);

    // Check bitwise equality (not just ==)
    long bitsDefault = Double.doubleToLongBits(nonStrictResult);
    long bitsStrict = Double.doubleToLongBits(strictResult);

    System.out.println("bitwise equal? " + (bitsDefault == bitsStrict));
  }

  // In Java 17 this is *already* strict, even without 'strictfp'.
  static double computeDefault(double base) {
    double x = base;
    // Chain a few operations to amplify rounding behavior
    for (int i = 0; i < 10; i++) {
      x = (x / 10.0) * 10.0;
    }
    return x;
  }

  // 'strictfp' is effectively redundant from Java 17 onward.
  static strictfp double computeStrict(double base) {
    double x = base;
    for (int i = 0; i < 10; i++) {
      x = (x / 10.0) * 10.0;
    }
    return x;
  }
}
