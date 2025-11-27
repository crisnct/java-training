package com.example.training.forkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinActionDemo {

  public static void main(String[] args) {
    ForkJoinPool pool = new ForkJoinPool(); // not AutoCloseable
    pool.invoke(new PrintAction(0, 10));
    pool.shutdown();
  }

  // ---------------------------------------------------------

  static class PrintAction extends RecursiveAction {

    private final int start;
    private final int end;

    PrintAction(int start, int end) {
      this.start = start;
      this.end = end;
    }

    @Override
    protected void compute() {
      int length = end - start;

      if (length <= 3) {
        for (int i = start; i < end; i++) {
          System.out.println("Value: " + i);
        }
        return;
      }

      int mid = start + length / 2;

      invokeAll(
          new PrintAction(start, mid),
          new PrintAction(mid, end)
      );
    }
  }
}
