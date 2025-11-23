package com.example.training;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerDemo {

  private static final SimpleDateFormat FMT = new SimpleDateFormat("HH:mm:ss.SSS");

  public static void main(String[] args) throws Exception {
    // true => thread daemon (nu blochează închiderea aplicației)
    Timer timer = new Timer(true);

    // 1) One-shot după 1s
    timer.schedule(new SafeTask("one-shot"), 1000L);

    // 2) Repetitiv FIXED-DELAY: 500ms delay inițial, apoi la 1000ms după terminarea task-ului
    timer.schedule(new CountingTask("fixed-delay", timer, 5), 500L, 1000L);

    // 3) Repetitiv FIXED-RATE: încearcă să mențină ritmul exact (poate „recupera” dacă a întârziat)
    Date startAt = new Date(System.currentTimeMillis() + 1500L); // start peste 1.5s
    timer.scheduleAtFixedRate(new CountingTask("fixed-rate", null, 5), startAt, 700L);

    // Ținem main-ul în viață ca să vedem execuțiile (doar pentru demo)
    Thread.sleep(7000L);
    timer.cancel(); // oprește timer-ul și eliberează resurse
    System.out.println(ts() + " [main] done");
  }

  // Task care prinde excepțiile ca să nu „omoare” firul Timer-ului
  static class SafeTask extends TimerTask {

    private final String name;

    SafeTask(String name) {
      this.name = name;
    }

    public void run() {
      try {
        System.out.println(ts() + " [" + name + "] run");
        // ... codul tău util ...
      } catch (Throwable t) {
        // NU lăsa excepții necontrolate: Timer thread moare în 1.3
        System.err.println(ts() + " [" + name + "] error: " + t);
      }
    }
  }

  // Task repetitiv cu numărătoare + anulare proprie
  static class CountingTask extends TimerTask {

    private final String name;
    private final Timer owner; // poate fi null
    private final int maxRuns;
    private int count = 0;

    CountingTask(String name, Timer owner, int maxRuns) {
      this.name = name;
      this.owner = owner;
      this.maxRuns = maxRuns;
    }

    public void run() {
      try {
        count++;
        System.out.println(ts() + " [" + name + "] run #" + count);
        // simulăm muncă 200ms
        try {
          Thread.sleep(200L);
        } catch (InterruptedException ignored) {
        }

        if (count >= maxRuns) {
          System.out.println(ts() + " [" + name + "] cancel self");
          cancel();            // oprește acest task
          if (owner != null) {
            // curăță task-urile anulate din coadă (opțional)
            int purged = owner.purge();
            System.out.println(ts() + " [timer] purged=" + purged);
          }
        }
      } catch (Throwable t) {
        System.err.println(ts() + " [" + name + "] error: " + t);
      }
    }
  }

  private static String ts() {
    return FMT.format(new Date());
  }
}
