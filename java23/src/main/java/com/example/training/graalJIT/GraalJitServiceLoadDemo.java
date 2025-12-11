package com.example.training.graalJIT;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Start with VM options -XX:+UnlockExperimentalVMOptions -XX:+UseGraalJIT and use a JDK from Oracle
///
/// Examples where Graal beats C2
/// - Inlining across deeper call chains
/// - Escape analysis that eliminates allocations C2 cannot
/// - Vectorization using the same basis as the Vector API
/// - Partial evaluation (unique to Graal)
/// - More efficient devirtualization
/// - Faster warm-up for microservices
/// - Better performance for virtual threads + structured concurrency
/// - ZGC + Graal is a strong combo
///
/// When C2 is still better
/// - Extremely CPU-bound raw math loops can still outperform Graal occasionally.
/// - Legacy codebases tuned for C2 sometimes run slightly faster under C2 until Graal "learns" them.
/// - C2 JIT has lower compilation overhead for trivial apps.
public class GraalJitServiceLoadDemo {

  private static final int TASK_COUNT = 50_000;
  private static final int ITEMS_PER_TASK = 256;

  public static void main(String[] args) {
    System.out.println("Running GraalJitServiceLoadDemo");
    System.out.println("JVM: " + System.getProperty("java.runtime.version"));
    System.out.println("GC : " + System.getProperty("java.vm.name"));
    System.out.println();

    GraalJitServiceLoadDemo demo = new GraalJitServiceLoadDemo();

    // Warmup to let JIT (C2 or Graal) optimize hot paths
    System.out.println("Warming up...");
    demo.runScenario(5_000);

    System.out.println("Running measured scenario with " + TASK_COUNT + " tasks...");
    Instant start = Instant.now();
    demo.runScenario(TASK_COUNT);
    Instant end = Instant.now();

    Duration duration = Duration.between(start, end);
    System.out.println("Total time: " + duration.toMillis() + " ms");
  }

  private void runScenario(int taskCount) {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      Random random = new Random(42);

      for (int i = 0; i < taskCount; i++) {
        int seed = random.nextInt();
        executor.submit(() -> processOrder(seed));
      }
    }
  }

  // Deep call chain that Graal can inline aggressively.
  private double processOrder(int seed) {
    Order order = OrderFactory.createOrder(seed);
    Order priced = PricingService.applyDiscounts(order);
    return PaymentService.computeFinalAmount(priced);
  }

  // Short-lived immutable object that Graal can often scalarize (no real heap allocation).
  private record Order(int[] itemQuantities, double[] itemBasePrices, double customerScore) {

  }

  private static final class OrderFactory {

    static Order createOrder(int seed) {
      Random random = new Random(seed);

      int[] quantities = new int[ITEMS_PER_TASK];
      double[] prices = new double[ITEMS_PER_TASK];

      for (int i = 0; i < ITEMS_PER_TASK; i++) {
        quantities[i] = 1 + random.nextInt(5);
        prices[i] = 5.0 + random.nextDouble(100.0);
      }

      double customerScore = random.nextDouble(1.0); // 0..1
      return new Order(quantities, prices, customerScore);
    }
  }

  private static final class PricingService {

    // Vectorizable loop: good candidate for Graal's vectorization.
    static Order applyDiscounts(Order order) {
      int[] q = order.itemQuantities();
      double[] p = order.itemBasePrices();
      double scoreFactor = 0.95 - (order.customerScore() * 0.1); // better score → better discount

      double[] discounted = new double[p.length];

      for (int i = 0; i < p.length; i++) {
        double base = p[i];
        double quantityFactor = quantityDiscountFactor(q[i]);
        discounted[i] = base * quantityFactor * scoreFactor;
      }

      return new Order(q, discounted, order.customerScore());
    }

    private static double quantityDiscountFactor(int quantity) {
      if (quantity >= 10) {
        return 0.8;
      }
      if (quantity >= 5) {
        return 0.9;
      }
      return 1.0;
    }
  }

  private static final class PaymentService {

    // More deep calls & simple math that JIT can aggressively inline and optimize.
    static double computeFinalAmount(Order order) {
      double subtotal = sum(order.itemQuantities(), order.itemBasePrices());
      double tax = computeTax(subtotal);
      double fee = computeProcessingFee(subtotal);
      return subtotal + tax + fee;
    }

    private static double sum(int[] quantities, double[] prices) {
      double result = 0.0;
      for (int i = 0; i < prices.length; i++) {
        result += quantities[i] * prices[i];
      }
      return result;
    }

    private static double computeTax(double amount) {
      return amount * 0.19;
    }

    private static double computeProcessingFee(double amount) {
      // Simulate a slightly more complex function, but still numeric
      double base = 0.50;
      double variable = Math.log1p(amount) * 0.01;
      return base + variable;
    }
  }
}
