package com.example.training.completableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo1 {

    public static void main(String[] args) throws Exception {
        basicAsync();
        chaining();
        combining();
    }

    // ------------------------------------------------------------
    // 1) Basic async computation (runAsync & supplyAsync)
    // ------------------------------------------------------------
    static void basicAsync() throws Exception {

        // Task without return value
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            sleep(500);
            System.out.println("Async task 1 finished.");
        });

        // Task with return value
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Result from async task 2";
        });

        future1.get();
        System.out.println(future2.get());
    }

    // ------------------------------------------------------------
    // 2) Chaining async steps (thenApply, thenAccept, thenRun)
    // ------------------------------------------------------------
    static void chaining() throws Exception {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Cristian";
        });

        CompletableFuture<Integer> result =
                future
                    .thenApply(name -> name.length())      // transform
                    .thenApply(len -> len * 2);            // another transform

        System.out.println("Chaining result = " + result.get());
    }

    // ------------------------------------------------------------
    // 3) Combining two futures (thenCombine)
    // ------------------------------------------------------------
    static void combining() throws Exception {

        CompletableFuture<Integer> priceFuture = CompletableFuture.supplyAsync(() -> {
            sleep(400);
            return 100;
        });

        CompletableFuture<Integer> discountFuture = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return 10;
        });

        // Combine results once both async tasks are done
        CompletableFuture<Integer> finalPrice =
                priceFuture.thenCombine(discountFuture, (price, discount) -> price - discount);

        System.out.println("Final price = " + finalPrice.get());
    }

    // ------------------------------------------------------------
    static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
