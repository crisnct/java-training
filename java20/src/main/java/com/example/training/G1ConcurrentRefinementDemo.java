package com.example.training;

import java.util.ArrayList;
import java.util.List;

//@formatter:off
/**
 * Generates write-heavy allocation patterns so G1 concurrent refinement
 * threads have work to do.
 *
 * Use JVM flags to see the Java 20 change:
 *
 * 1) Show obsolete tuning flags for the old controller:
 *
 *   java -Xms256m -Xmx256m -XX:+UseG1GC \
 *        -XX:G1ConcRefinementGreenZone=8 \
 *        -XX:G1ConcRefinementYellowZone=16 \
 *        -XX:G1ConcRefinementRedZone=32 \
 *        -XX:G1ConcRefinementThresholdStep=1 \
 *        -Xlog:gc+refine=debug \
 *        com.example.training.G1ConcurrentRefinementDemo
 *
 *   On Java 20+ these options are OBSOLETE and only produce warnings;
 *   they no longer control refinement threads.
 *
 * 2) Let the new controller show its behavior:
 *
 *   java -Xms256m -Xmx256m -XX:+UseG1GC \
 *        -Xlog:gc+refine=debug \
 *        com.example.training.G1ConcurrentRefinementDemo
 *
 *   Compare gc+refine logs with pre-20: fewer refinement threads,
 *   fewer activity spikes, more delayed refinement.
 */
//@formatter:on
public class G1ConcurrentRefinementDemo {

    private static final List<byte[]> holder = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting allocation loop. Watch GC logs (gc+refine).");

        for (int iteration = 0; iteration < 120; iteration++) {
            allocateAndDirty();
            if (iteration % 10 == 0) {
                System.out.println("Iteration " + iteration + " (holder size=" + holder.size() + ")");
            }
            Thread.sleep(200L);
        }

        System.out.println("Finished. Check GC log output for refinement activity.");
    }

    private static void allocateAndDirty() {
        // Allocate some objects and keep a subset alive so they can age
        for (int i = 0; i < 256; i++) {
            byte[] block = new byte[1024 * 256]; // 256 KB
            // dirty memory to produce card marks
            for (int j = 0; j < block.length; j += 64) {
                block[j] = (byte) (block[j] + 1);
            }
            holder.add(block);
        }

        // Drop part of the list so GC has work, but keep enough to grow old-gen
        if (holder.size() > 4_000) {
            holder.subList(0, 2_000).clear();
        }
    }
}
