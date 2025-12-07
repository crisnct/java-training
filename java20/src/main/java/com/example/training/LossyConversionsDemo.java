package com.example.training;//@formatter:off
/**
 * Java 20 "lossy-conversions" lint demo.
 *
 * See -Xlint:lossy-conversions from pom.xml
 * Expect warning:
 *   "possible lossy conversion from int to byte"
 *
 * Use this for main method and warning dissapear
 * @SuppressWarnings("lossy-conversions")
 */
//@formatter:on
public class LossyConversionsDemo {

  //@SuppressWarnings("lossy-conversions")
  public static void main(String[] args) {
    byte b = 100;
    // This causes the Java 20 lint warning (lossy-conversions):
    // b += 200; implicitly does (byte)(b + 200)
    b += 200;
    System.out.println("b = " + b);
  }
}
