package com.example.training;

public class ThreadsDaemonDemo {

  public static void main(String[] args) throws InterruptedException {
    Runnable r1 = new Runnable() {
      public void run() {
        try {
          System.out.println("Non-daemon thread started");
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        } finally {
          System.out.println("Non-daemon thread finalized");
        }
      }
    };

    Runnable r2 = new Runnable() {
      public void run() {
        try {
          System.out.println("Daemon thread started");
          while (true) {
            Thread.sleep(5000);
          }
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        } finally {
          System.out.println("Daemon thread finalized");
        }
      }
    };

    Thread t1 = new Thread(r1);
    t1.setDaemon(false);
    t1.start();

    Thread t2 = new Thread(r2);
    t2.setDaemon(true);
    t2.start();

  }
}
