package com.example.training;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureEnhancements {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return "Value computed";
    })
        .orTimeout(1, TimeUnit.SECONDS)
        .exceptionally(ex -> "Timeout triggered");

    String value = future.get();
    System.out.println(value);
  }
}
