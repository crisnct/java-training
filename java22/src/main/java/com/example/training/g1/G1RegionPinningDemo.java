package com.example.training.g1;

import java.time.Instant;

/**
 * 1. Generate .h file. Execute this in g1 folder
 * {@snippet :
 *  javac -h . G1RegionPinningDemo.java
 *} 2. Install MinGW-w64 using cursor AI and then install pacman
 * {@snippet :
 *    pacman -S --needed mingw-w64-ucrt-x86_64-gcc
 *}
 * <p>
 * 3. Generate dll file
 * {@snippet :
 *    gcc -I"C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot\include" -I"C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot\include\win32" -shared -O2 -o g1pinning.dll g1_pinning.c
 *}
 */
public class G1RegionPinningDemo {

  static {
    // Option A: search by name in java.library.path
    System.loadLibrary("g1pinning");
    // Option B (absolute path) if you don’t want to touch java.library.path:
    // System.load("D:\\Workspace\\your-project\\native\\g1pinning.dll");
  }

  private static native void holdArrayCritical(byte[] array, long millis);

  public static void main(String[] args) throws InterruptedException {
    final int secondsToPin = 5;
    final byte[] big = new byte[64 * 1024 * 1024];

    Thread pressure = new Thread(G1RegionPinningDemo::allocatePressure, "alloc-pressure");
    pressure.setDaemon(true);
    pressure.start();

    System.out.println(ts() + " PIN START");
    holdArrayCritical(big, secondsToPin * 1000L);
    System.out.println(ts() + " PIN END");
  }

  private static void allocatePressure() {
    final int chunk = 1 * 1024 * 1024;
    while (true) {
      byte[][] junk = new byte[64][];
      for (int i = 0; i < junk.length; i++) {
        junk[i] = new byte[chunk];
      }
      try {
        Thread.sleep(5);
      } catch (InterruptedException ignored) {
      }
    }
  }

  private static String ts() {
    return "[" + Instant.now() + "]";
  }
}
