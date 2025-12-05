package com.example.training;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 13 demo for JEP 351 (ZGC can uncommit unused memory).
 * 1) Allocates a big chunk of heap in ~8 MiB blocks, touching each to ensure commit.
 * 2) Drops references, triggers GC, then idles so ZGC can uncommit.
 * 3) Prints PID so you can observe RSS (e.g., top/htop/taskmgr) and use jcmd.
 */
public class ZgcUncommitDemo {

    public static void main(String[] args) throws Exception {
        long pid = ProcessHandle.current().pid();
        System.out.println("PID=" + pid);
        System.out.println("JVM Args: " + ManagementFactory.getRuntimeMXBean().getInputArguments());
        printHeapUsage("start");

        // Target allocation size (adjust if you hit OOME). 1.5 GiB is usually safe with -Xmx4g.
        final long targetBytes = 1_500L * 1024 * 1024;
        final int blockSize = 8 * 1024 * 1024; // 8 MiB
        final List<byte[]> blocks = new ArrayList<>();

        // 1) Allocate and touch to force physical commit.
        long allocated = 0;
        while (allocated < targetBytes) {
            byte[] b = new byte[blockSize];
            b[0] = 1; // touch to force page materialization
            b[b.length - 1] = 1;
            blocks.add(b);
            allocated += blockSize;
        }
        System.out.println("Allocated ~" + mb(allocated) + " MiB in " + blocks.size() + " blocks");
        printHeapUsage("after allocate");

        // 2) Drop references and ask for GC
        blocks.clear();
        System.out.println("Cleared references; requesting GC...");
        System.gc();

        // 3) Idle so ZGC can detect unused regions and uncommit them.
        //    ZUncommitDelay is set to 5s via VM flag; sleep a bit longer and sample heap.
        for (int i = 1; i <= 6; i++) {
            Thread.sleep(Duration.ofSeconds(5).toMillis());
            printHeapUsage("idle t=" + (i * 5) + "s");
        }

        System.out.println("Done. Check logs for 'uncommit' and observe process RSS shrink.");
    }

    private static void printHeapUsage(String label) {
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        long max = Runtime.getRuntime().maxMemory();
        System.out.printf("Heap @ %s | used=%d MiB, committed=%d MiB, max=%d MiB%n",
                label, mb(used), mb(total), mb(max));
    }

    private static long mb(long bytes) {
        return bytes / (1024 * 1024);
    }
}
