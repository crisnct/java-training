package com.example.training;

import org.openjdk.jol.info.ClassLayout;
import java.util.ArrayList;
import java.util.List;

//@formatter:off
/**
 * CompactHeadersDemo
 *
 * Goal:
 *  - Show header size & total footprint differences with Compact Object Headers (COH).
 *  - Run twice: with and without -XX:+UseCompactObjectHeaders.
 *
 * What you’ll see:
 *  - Smaller header (on many objects) when COH is enabled.
 *  - Lower retained heap after allocating lots of tiny objects.
 *
 * Run (JDK 24):
 *   # Baseline (COH off)
 *   java -XX:-UseCompactObjectHeaders -Xms1g -Xmx1g -cp target/classes;target/dependency/* CompactHeadersDemo
 *
 *   # COH on (experimental)
 *   java -XX:+UseCompactObjectHeaders -Xms1g -Xmx1g -cp target/classes;target/dependency/* CompactHeadersDemo
 *
 * Tips:
 *  - Keep the classpath purely jars/folders of this project (no agents) for stable numbers.
 *  - Use same heap flags across runs to make deltas clear.
 */
//@formatter:on
public class CompactHeadersDemo {

    // Tiny objects amplify header differences
    static final class Tiny {
        int a; // 4 bytes
        int b; // 4 bytes
        // 8 bytes of fields total; header dominates
    }

    public static void main(String[] args) throws Exception {
        // 1) Print precise object layouts (requires JOL)
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println("\n--- Layouts ---");
        System.out.println(ClassLayout.parseClass(Object.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Tiny.class).toPrintable());
        System.out.println(ClassLayout.parseClass(int[].class).toPrintable());
        System.out.println(ClassLayout.parseClass(Object[].class).toPrintable());

        // 2) Footprint experiment: allocate many Tiny objects
        //    We force GC before/after to estimate retained size.
        final int N = 5_000_000;

        System.gc(); sleep(500);
        long before = used();

        List<Tiny> list = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            Tiny t = new Tiny();
            t.a = i;
            t.b = -i;
            list.add(t);
        }

        System.gc(); sleep(1000);
        long after = used();

        long retained = after - before;
        System.out.printf("%nAllocated %,d Tiny objects%n", N);
        System.out.printf("Retained heap ≈ %,d bytes (%.2f MB)%n",
                retained, retained / (1024.0 * 1024.0));
        System.out.println("\nHint: Re-run with -XX:+UseCompactObjectHeaders and compare.");
    }

    private static long used() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
