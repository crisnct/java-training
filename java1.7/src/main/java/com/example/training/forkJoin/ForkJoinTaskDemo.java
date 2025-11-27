package com.example.training.forkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTaskDemo {

  public static void main(String[] args) {
    ForkJoinPool pool = new ForkJoinPool();
    int[] numbers = new int[1_000_000];
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = i + 1;
    }

    int sum = 0;
    long startTime = System.currentTimeMillis();
    for (int number : numbers) {
      sum += number;
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Duration of sum via one thread: " + (endTime - startTime));

    startTime = System.currentTimeMillis();
    int result = pool.invoke(new SumTask(numbers, 0, numbers.length));
    endTime = System.currentTimeMillis();
    System.out.println("Duration of sum via recursion: " + (endTime - startTime));

    pool.shutdown();

    System.out.println("Sum1: " + sum);
    System.out.println("Sum2: " + result);
  }

  // ---------------------------------------------------------

  static class SumTask extends RecursiveTask<Integer> {

    private final int[] data;
    private final int start;
    private final int end;

    SumTask(int[] data, int start, int end) {
      this.data = data;
      this.start = start;
      this.end = end;
    }

    @Override
    protected Integer compute() {
      int length = end - start;

      // Small task → compute directly
      if (length <= 3) {
        int sum = 0;
        for (int i = start; i < end; i++) {
          sum += data[i];
        }
        return sum;
      }

      // Split
      int mid = start + length / 2;

      SumTask left = new SumTask(data, start, mid);
      SumTask right = new SumTask(data, mid, end);

      left.fork();

      return left.join() + right.compute();
    }
  }
}
