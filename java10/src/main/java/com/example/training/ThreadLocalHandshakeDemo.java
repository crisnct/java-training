package com.example.training;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

/**
 * Before Java 10, many operations that needed to look at thread state (stack, registers, safepoint polling etc.) were implemented by:
 * bringing all threads to a safepoint (global stop-the-world),
 * doing the thing (stack walk, deopt, etc.),
 * then resuming everything.
 * Java 10 added Thread-Local Handshakes:
 * The JVM can now send a handshake request to one specific Java thread.
 * That thread runs a small callback at a safepoint-safe moment.
 * Other threads do not need to be stopped.
 */
public class ThreadLocalHandshakeDemo {

  public static void main(String[] args) throws Exception {
    // Worker 1: busy computation
    Thread worker1 = new Thread(() -> {
      long sum = 0;
      System.out.println("Thread 1 started");
      while (true) {
        sum += System.nanoTime();
        if (sum % 10_000_000_000L == 0) {
          //System.out.println("[worker-1] still running, sum=" + sum);
        }
      }
    }, "worker-1");

    // Worker 2: also busy, but prints at a different rhythm
    Thread worker2 = new Thread(() -> {
      long counter = 0;
      System.out.println("Thread 2 started");
      while (true) {
        counter++;
        if (counter % 50_000_000 == 0) {
          //System.out.println("[worker-2] counter=" + counter);
        }
      }
    }, "worker-2");

    worker1.start();
    worker2.start();

    // Give workers some time to start and get busy
    TimeUnit.SECONDS.sleep(5);

    // Monitor thread: repeatedly inspects ONLY worker-1
    ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
    long targetId = worker1.getId();

    for (int i = 0; i < 10; i++) {
      ThreadInfo info = mxBean.getThreadInfo(targetId, Integer.MAX_VALUE);

      System.out.println("------ Inspection #" + i + " of " + worker1.getName() + " ------");
      if (info != null) {
        System.out.println("Thread state: " + info.getThreadState());
        System.out.println("Top of stack:");
        StackTraceElement[] stack = info.getStackTrace();
        if (stack.length > 0) {
          System.out.println("  at " + stack[0]);
        } else {
          System.out.println("  (no stack frames)");
        }
      } else {
        System.out.println("Thread info not available.");
      }
      System.out.println("-------------------------------------------");

      TimeUnit.SECONDS.sleep(1);
    }

    System.out.println("Main thread done. Workers still running...");
  }
}
