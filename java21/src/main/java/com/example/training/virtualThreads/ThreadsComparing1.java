package com.example.training.virtualThreads;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//@formatter:off
/**
 * Virtual threads vs platform threads: throughput under blocking.
 *
 * Expectation:
 * - Platform: total time roughly (tasks / poolSize) * sleepMs.
 * - Virtual: total time ~ sleepMs (plus overhead), because each task gets its own v-thread.
 *
 * Virtual threads are so fast because they avoid paying OS-thread costs when you block.
 */
//@formatter:on
public class ThreadsComparing1 {

  enum THREADS_TYPE {
    PLATFORM,
    VIRTUAL
  }

  public static void main(String[] args) throws Exception {
    Config config = new Config(5000, 200, 32);
    for (THREADS_TYPE type : THREADS_TYPE.values()) {
      System.out.println("----------------------------");
      System.out.println(
          "Threads=" + type.name().toLowerCase() + " | Tasks=" + config.tasks + " | sleepMs=" + config.sleepMs + " | poolSize=" + config.platformPoolSize);
      ExecutorService executor = createExecutor(config, type);
      ThreadMXBean tbean = ManagementFactory.getThreadMXBean();

      List<Future<?>> futures = new ArrayList<>(config.tasks);
      Instant t0 = Instant.now();
      PeakThreadsTracker tracker = new PeakThreadsTracker(tbean);
      tracker.start();

      try (executor) {
        for (int i = 0; i < config.tasks; i++) {
          futures.add(executor.submit(new SleepTask(config.sleepMs)));
        }
        // Wait for all
        for (Future<?> f : futures) {
          f.get();
        }
      } finally {
        tracker.stop();
      }

      Duration took = Duration.between(t0, Instant.now());
      System.out.println("Duration: " + took.toMillis() + " ms");
      System.out.println("Peak live threads observed: " + tracker.getPeak());
    }
  }

  private static ExecutorService createExecutor(Config c, THREADS_TYPE type) {
    return switch (type) {
      case THREADS_TYPE.VIRTUAL -> Executors.newVirtualThreadPerTaskExecutor();
      case THREADS_TYPE.PLATFORM -> new ThreadPoolExecutor(
          c.platformPoolSize,
          c.platformPoolSize,
          30L, TimeUnit.SECONDS,
          new LinkedBlockingQueue<>(),
          new ThreadFactory() {
            private final ThreadFactory df = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
              Thread t = df.newThread(r);
              t.setName("platform-" + t.getId());
              t.setDaemon(false);
              return t;
            }
          }
      );
    };
  }

  private static final class SleepTask implements Callable<Void> {

    private final long sleepMs;

    private SleepTask(long sleepMs) {
      this.sleepMs = sleepMs;
    }

    @Override
    public Void call() {
      try {
        Thread.sleep(sleepMs);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      return null;
    }
  }

  private static final class PeakThreadsTracker {

    private final ThreadMXBean bean;
    private final ScheduledExecutorService ses;
    private volatile int peak = 0;
    private ScheduledFuture<?> future;

    private PeakThreadsTracker(ThreadMXBean bean) {
      this.bean = bean;
      this.ses = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "thread-peak-tracker");
        t.setDaemon(true);
        return t;
      });
    }

    void start() {
      future = ses.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
          int live = bean.getThreadCount();
          if (live > peak) {
            peak = live;
          }
        }
      }, 0, 10, TimeUnit.MILLISECONDS);
    }

    void stop() {
      if (future != null) {
        future.cancel(true);
      }
      ses.shutdownNow();
    }

    int getPeak() {
      return peak;
    }
  }

  private record Config(int tasks, long sleepMs, int platformPoolSize) {

  }


}
