package com.example.training;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

//@formatter:off
/**
 * Simple demo used together with JVM options to observe
 * ASLR(ArchiveRelocationMode) behavior for the CDS archive in Java 20.0.2+.
 *
 * The code itself just shows the process ID and JVM
 * input arguments, so you can correlate logs with this run.
 * VM args:
 * -Xshare:on -Xlog:cds=debug -XX:+UnlockDiagnosticVMOptions -XX:ArchiveRelocationMode=0
 *
 * Practical case:
 * You are testing two different GC settings and want exact CPU hotspot comparison.
 * Without ASLR control, half your profile tool output is noise from address randomization.
 */
//@formatter:on
public class CdsAslrDemo {

  public static void main(String[] args) throws InterruptedException {
    RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
    String runtimeName = runtimeMxBean.getName();
    System.out.println("Runtime name (pid@host): " + runtimeName);
    System.out.println("JVM input arguments: " + runtimeMxBean.getInputArguments());
    System.out.println("Sleeping to keep the JVM alive. Check the CDS logs...");

    Thread.sleep(600_000L);
  }
}
