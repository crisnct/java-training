package com.example.training.completableFuture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Use join() inside chains, get() at boundaries.
 * Use thenCompose() instead of blocking.
 * Always define your own executor for I/O-heavy async work.
 * Handle errors with exceptionally() or handle().
 * allOf() never gives results directly — you join each one.
 * Don’t block the common pool.
 * CompletableFuture is async, not reactive.
 * Never block inside a thread pool that is also responsible for running the async stages.
 * Otherwise you risk thread starvation and deadlocks. Example of blocking:
 * CompletableFuture<String> f1 =
 *     CompletableFuture.supplyAsync(() -> slowOperation(), pool);
 * CompletableFuture<String> f2 =
 *     CompletableFuture.supplyAsync(() -> f1.get(), pool);  // BAD
 */
public class CompletableFutureDemo2 {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(4);

    try {
      long start = System.currentTimeMillis();

      // 1) Fetch user
      CompletableFuture<User> userFuture =
          CompletableFuture.supplyAsync(() -> UserService.fetchUser(42), executor);

      // 2) Once user is known, fetch his orders (dependent async call)
      CompletableFuture<List<Order>> ordersFuture =
          userFuture.thenCompose(user ->
              CompletableFuture.supplyAsync(() -> OrderService.fetchOrders(user), executor)
          );

      // 3) In parallel, fetch recommendations (independent async call)
      CompletableFuture<List<String>> recommendationsFuture =
          CompletableFuture.supplyAsync(() ->
              RecommendationService.fetchRecommendations(42), executor);

      // 4) Wait for ALL futures to complete, then build the Dashboard
      CompletableFuture<Dashboard> dashboardFuture =
          CompletableFuture.allOf(userFuture, ordersFuture, recommendationsFuture)
              .thenApply(voidResult -> {
                User user = userFuture.join();
                List<Order> orders = ordersFuture.join();
                List<String> recs = recommendationsFuture.join();
                return new Dashboard(user, orders, recs);
              })
              .exceptionally(ex -> {
                System.out.println("Error building dashboard: " + ex.getMessage());
                return Dashboard.empty();
              });

      Dashboard dashboard = dashboardFuture.get();
      long elapsed = System.currentTimeMillis() - start;

      System.out.println("Dashboard = " + dashboard);
      System.out.println("Built in " + elapsed + " ms");
    } finally {
      executor.shutdown();
    }
  }

  // ----------------------------------------------------------------------
  // Mock domain classes and services
  // ----------------------------------------------------------------------

  static class User {

    final int id;
    final String name;

    User(int id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    public String toString() {
      return "User{id=" + id + ", name='" + name + "'}";
    }
  }

  static class Order {

    final int id;
    final String item;

    Order(int id, String item) {
      this.id = id;
      this.item = item;
    }

    @Override
    public String toString() {
      return "Order{id=" + id + ", item='" + item + "'}";
    }
  }

  static class Dashboard {

    final User user;
    final List<Order> orders;
    final List<String> recommendations;

    Dashboard(User user, List<Order> orders, List<String> recommendations) {
      this.user = user;
      this.orders = orders;
      this.recommendations = recommendations;
    }

    static Dashboard empty() {
      return new Dashboard(null, Collections.<Order>emptyList(), Collections.<String>emptyList());
    }

    @Override
    public String toString() {
      return "Dashboard{" +
          "user=" + user +
          ", orders=" + orders +
          ", recommendations=" + recommendations +
          '}';
    }
  }

  // Simulated external services
  static class UserService {

    static User fetchUser(int userId) {
      sleep(300);
      return new User(userId, "Cristian");
    }
  }

  static class OrderService {

    static List<Order> fetchOrders(User user) {
      sleep(400);
      return Arrays.asList(
          new Order(1, "Laptop"),
          new Order(2, "Book")
      );
    }
  }

  static class RecommendationService {

    static List<String> fetchRecommendations(int userId) {
      sleep(250);
      return Arrays.asList("Mouse", "Keyboard", "Headphones");
    }
  }

  private static void sleep(long ms) {
    try {
      TimeUnit.MILLISECONDS.sleep(ms);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }
}
