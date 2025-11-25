package com.example.training;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class CyclicBarrierExample {

  public static void main(String[] args) {
    int participants = 3;

    CyclicBarrier barrier = new CyclicBarrier(
        participants,
        new Runnable() {
          public void run() {
            System.out.println("All workers reached the barrier. Proceeding together.");
          }
        }
    );

    for (int i = 1; i <= participants; i++) {
      Thread worker = new Thread(new Worker(i, barrier));
      worker.start();
    }
  }

  static class Worker implements Runnable {

    private final int id;
    private final CyclicBarrier barrier;

    Worker(int id, CyclicBarrier barrier) {
      this.id = id;
      this.barrier = barrier;
    }

    public void run() {
      try {
        System.out.println("Worker " + id + " is doing some work...");
        Thread.sleep(1000 + id * 500);

        System.out.println("Worker " + id + " reached the barrier.");
        barrier.await();

        System.out.println("Worker " + id + " continues after the barrier.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
