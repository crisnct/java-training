package com.example.training;

public class TimeBasedVersionDemo {

  public static void main(String[] args) {
    Runtime.Version version = Runtime.version();

    int feature = version.feature();   // e.g. 10
    int interim = version.interim();   // usually 0
    int update = version.update();    // e.g. 2 in 10.0.2
    int patch = version.patch();     // often 0

    System.out.println("Raw java.version: " + System.getProperty("java.version"));
    System.out.println("Parsed via Runtime.Version:");
    System.out.println("  feature = " + feature);
    System.out.println("  interim = " + interim);
    System.out.println("  update  = " + update);
    System.out.println("  patch   = " + patch);
    System.out.println("  build   = " + version.build().orElse(null));
    System.out.println("  pre     = " + version.pre().orElse("<none>"));

    // Example: behavior based on time-based version
    if (feature >= 10) {
      System.out.println("Running on time-based release JDK (10+).");
    } else {
      System.out.println("Running on legacy version scheme (pre-10).");
    }

    // Example: require at least 10.0.2
    if (feature > 10 || (feature == 10 && update >= 2)) {
      System.out.println("Meets minimum recommended version >= 10.0.2");
    } else {
      System.out.println("Warning: recommended JDK version is at least 10.0.2");
    }
  }
}
