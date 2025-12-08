package com.example.training.virtualThreads;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

//@formatter:off
/**
 * Java 21 – Virtual threads working safely on the same in-memory Map.
 *
 * Scenario: an in-memory inventory updated by many concurrent “orders”.
 * We use:
 *  - Virtual threads (cheap to create/block).
 *  - ConcurrentHashMap + LongAdder (no synchronized blocks, no pinning).
 *  - CountDownLatch for completion (no streams, no lambdas).
 *
 * Run and observe:
 *  - Thousands of tasks update the same keys with no contention bugs.
 *  - Final inventory equals initial + all applied deltas.
 */
//@formatter:on
public class CoffeeShop {

  public static void main(String[] args) throws Exception {

    Inventory inventory = new Inventory();
    inventory.putInitial("COFFEE_BEANS", 10_000);
    inventory.putInitial("GREEN_TEA", 5_000);
    inventory.putInitial("CACAO", 3_000);
    inventory.putInitial("CARDAMOM", 1_000);

    int totalTasks = 100_000;              // number of “orders”
    int readers = 50_000;               // parallel readers computing totals
    CountDownLatch writersDone = new CountDownLatch(totalTasks);
    CountDownLatch readersDone = new CountDownLatch(readers);

    long t0 = System.nanoTime();

    // One task = one virtual thread
    try (ExecutorService vexec = Executors.newVirtualThreadPerTaskExecutor()) {
      // Writers
      for (int i = 0; i < totalTasks; i++) {
        vexec.submit(new WriterTask(inventory, writersDone));
      }
      // Readers (simulate dashboards pulling totals in parallel)
      for (int i = 0; i < readers; i++) {
        vexec.submit(new ReaderTask(inventory, readersDone));
      }

      // Wait for completion
      writersDone.await();
      readersDone.await();
    }

    long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);

    // Verify and print snapshot
    Snapshot snapshot = inventory.snapshot();
    System.out.println("=== Inventory snapshot ===");
    printLine("COFFEE_BEANS", snapshot);
    printLine("GREEN_TEA", snapshot);
    printLine("CACAO", snapshot);
    printLine("CARDAMOM", snapshot);

    // Consistency check
    boolean ok = inventory.verifyTotals();
    System.out.println("Consistent totals: " + ok + " | tasks=" + totalTasks + " | readers=" + readers + " | took=" + elapsedMs + " ms");
  }

  private static void printLine(String key, Snapshot s) {
    System.out.println(key + " | initial=" + s.initial(key) + " | delta=" + s.delta(key) + " | current=" + s.current(key));
  }

  // ---------------- Core data structure ----------------

  private static final class Inventory {

    private final ConcurrentHashMap<String, LongAdder> current = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> deltas = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> initial = new ConcurrentHashMap<>();

    void putInitial(String sku, int qty) {
      initial.put(sku, qty);
      LongAdder a = new LongAdder();
      a.add(qty);
      current.put(sku, a);
      deltas.put(sku, new LongAdder());
    }

    void applyDelta(String sku, int delta) {
      // current += delta
      LongAdder c = current.get(sku);
      if (c == null) {
        return; // unknown SKU
      }
      c.add(delta);

      // track total applied delta for verification
      LongAdder d = deltas.get(sku);
      if (d != null) {
        d.add(delta);
      }
    }

    int getCurrent(String sku) {
      LongAdder a = current.get(sku);
      return a == null ? 0 : (int) a.sum();
    }

    int getDelta(String sku) {
      LongAdder a = deltas.get(sku);
      return a == null ? 0 : (int) a.sum();
    }

    int getInitial(String sku) {
      Integer v = initial.get(sku);
      return v == null ? 0 : v;
    }

    Snapshot snapshot() {
      return new Snapshot(initial, current, deltas);
    }

    boolean verifyTotals() {
      for (Map.Entry<String, Integer> e : initial.entrySet()) {
        String k = e.getKey();
        int expect = e.getValue() + getDelta(k);
        if (getCurrent(k) != expect) {
          return false;
        }
      }
      return true;
    }
  }

  private static final class Snapshot {

    private final Map<String, Integer> initial;
    private final Map<String, LongAdder> current;
    private final Map<String, LongAdder> deltas;

    Snapshot(Map<String, Integer> initial, Map<String, LongAdder> current, Map<String, LongAdder> deltas) {
      this.initial = copyInitial(initial);
      this.current = copyAdderMap(current);
      this.deltas = copyAdderMap(deltas);
    }

    int initial(String key) {
      return val(initial.get(key));
    }

    int current(String key) {
      return sum(current.get(key));
    }

    int delta(String key) {
      return sum(deltas.get(key));
    }

    private static int val(Integer v) {
      return v == null ? 0 : v;
    }

    private static int sum(LongAdder a) {
      return a == null ? 0 : (int) a.sum();
    }

    private static Map<String, Integer> copyInitial(Map<String, Integer> src) {
      ConcurrentHashMap<String, Integer> dst = new ConcurrentHashMap<>();
      for (Map.Entry<String, Integer> e : src.entrySet()) {
        dst.put(e.getKey(), e.getValue());
      }
      return dst;
    }

    private static Map<String, LongAdder> copyAdderMap(Map<String, LongAdder> src) {
      ConcurrentHashMap<String, LongAdder> dst = new ConcurrentHashMap<>();
      for (Map.Entry<String, LongAdder> e : src.entrySet()) {
        LongAdder copy = new LongAdder();
        copy.add(e.getValue().sum());
        dst.put(e.getKey(), copy);
      }
      return dst;
    }
  }

  // ---------------- Tasks ----------------

  private static final class WriterTask implements Runnable {

    private static final String[] SKUS = {"COFFEE_BEANS", "GREEN_TEA", "CACAO", "CARDAMOM"};
    private final Inventory inventory;
    private final CountDownLatch done;
    private final Random rnd = new Random();

    WriterTask(Inventory inventory, CountDownLatch done) {
      this.inventory = inventory;
      this.done = done;
    }

    @Override
    public void run() {
      try {
        // choose a random SKU and a small delta (-3..+3, excluding 0)
        String sku = SKUS[rnd.nextInt(SKUS.length)];
        int delta = randomDelta();
        inventory.applyDelta(sku, delta);

        // pretend there is brief waiting (e.g., downstream ack); this is cheap with virtual threads
        try {
          Thread.sleep(rnd.nextInt(3));
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      } finally {
        done.countDown();
      }
    }

    private int randomDelta() {
      int d = rnd.nextInt(7) - 3; // -3..+3
      if (d == 0) {
        d = 1;          // avoid no-op
      }
      return d;
    }
  }

  private static final class ReaderTask implements Runnable {

    private static final String[] SKUS = {"COFFEE_BEANS", "GREEN_TEA", "CACAO", "CARDAMOM"};
    private final Inventory inventory;
    private final CountDownLatch done;

    ReaderTask(Inventory inventory, CountDownLatch done) {
      this.inventory = inventory;
      this.done = done;
    }

    @Override
    public void run() {
      try {
        // compute a cheap “total on hand” over all SKUs
        int total = 0;
        int i;
        for (i = 0; i < SKUS.length; i++) {
          total += inventory.getCurrent(SKUS[i]);
        }
        // touch the value so JIT keeps the reads
        if (total == Integer.MIN_VALUE) {
          System.out.print("");
        }
      } finally {
        done.countDown();
      }
    }
  }
}
