
package com.example.training;

//@formatter:off
import java.util.ArrayList;import java.util.List; /**
 * Demonstrates the effect of Compact Object Headers (JEP 450, final in Java 25)
 * by measuring heap usage before and after allocating millions of small objects.
 *
 * No external libraries or JVM flags required.
 *
 * What to expect:
 *  - On Java 25, total heap usage will be noticeably smaller
 *    compared to Java 21 or older, because headers now take
 *    fewer bytes per object (12 instead of 16 bytes in most cases).
 */
//@formatter:on
public class CompactHeadersNoJolDemo {

  record Entry(int id, double balance, String name) {
  }

  void main(String[] ___) {
    System.gc();
    long before = usedMemory();

    List<Entry> entries = new ArrayList<>();
    for (int i = 0; i < 5_000_000; i++) {
      entries.add(new Entry(i, Math.random() * 1000, "C" + i));
    }

    System.gc();
    long after = usedMemory();

    System.out.printf("Created %,d Entry objects.%n", entries.size());
    System.out.printf("Approx heap used: %.2f MB%n", (after - before) / 1_000_000.0);
  }

  private static long usedMemory() {
    Runtime rt = Runtime.getRuntime();
    return rt.totalMemory() - rt.freeMemory();
  }
}
