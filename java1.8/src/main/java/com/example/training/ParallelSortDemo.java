package com.example.training;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ParallelSortDemo {

  public static final int STREAM_SIZE = 10_000_000;

  public static void main(String[] args) {
    int[] a = ThreadLocalRandom.current().ints(STREAM_SIZE).toArray();
    long start = System.currentTimeMillis();
    Arrays.parallelSort(a);
    System.out.println("Duration parallel sort: "+ (System.currentTimeMillis()- start));

    a = ThreadLocalRandom.current().ints(STREAM_SIZE).toArray();
    start = System.currentTimeMillis();
    Arrays.sort(a);
    System.out.println("Duration normal sort: "+ (System.currentTimeMillis()- start));
  }
}
