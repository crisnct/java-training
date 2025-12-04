package com.example.training;

/**
 * JDK Flight Recorder (JFR)
 * JFR is part of OpenJDK.
 * Fully free, production-ready.
 * Gives extremely low-overhead profiling: GC, threads, locks, IO, allocations, CPU, JIT, etc.
 *
 * Run with arguments
 *  -XX:StartFlightRecording=duration=30s,filename=recording.jfr
 */
public class JfrDemo {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5_000_000; i++) {
            byte[] data = new byte[1024];
        }
        Thread.sleep(3000);
        System.out.println("Workload completed.");
    }
}
