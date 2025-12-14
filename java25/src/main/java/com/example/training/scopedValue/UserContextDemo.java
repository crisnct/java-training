package com.example.training.scopedValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//@formatter:off
/**
 * Real-world example of ScopedValue (final, Java 25).
 *
 * Scenario:
 *  - A request handler authenticates a user.
 *  - The user context is stored in a ScopedValue for this request only.
 *  - Downstream services (logging, database, etc.) can read the user info
 *    without it being passed as a method parameter.
 *
 * Benefits:
 *  - No ThreadLocal leaks.
 *  - No need to explicitly clear or reset context.
 *  - Context automatically disappears after the scope ends.
 *
 * Before Java 25, everyone used ThreadLocal<User> for passing things like:
 * user/session info
 * trace IDs
 * database transaction context
 * locale/timezone
 *
 * Example:
 * private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();
 *
 * You’d set it at request start:
 * CURRENT_USER.set(user);
 *
 */
//@formatter:on
public class UserContextDemo {

  // Immutable contextual variable to hold user info
  private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

  static void main() throws Exception {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

      executor.submit(() -> handleRequest(new User("alice", "ADMIN")));
      executor.submit(() -> handleRequest(new User("bob", "VIEWER")));

      Thread.sleep(1000); // wait for requests to finish
    }
  }

  private static void handleRequest(User user) {
    ScopedValue.where(CURRENT_USER, user).run(() -> {
      log("Incoming request for user: " + user.username());
      processBusinessOperation();
      log("Request completed for " + user.username());
    });
  }

  private static void processBusinessOperation() {
    // Somewhere deep in the call chain — no direct access to CURRENT_USER passed in
    log("Validating permissions...");
    if (!CURRENT_USER.get().role().equals("ADMIN")) {
      log("Access denied.");
      return;
    }

    log("Performing sensitive operation...");
    simulateDbWrite();
    log("Operation finished.");
  }

  private static void simulateDbWrite() {
    log("Saving record to DB...");
    try {
      Thread.sleep(200);
    } catch (InterruptedException ignored) {
    }
    log("DB save complete.");
  }

  private static void log(String msg) {
    User user = CURRENT_USER.isBound() ? CURRENT_USER.get() : new User("anonymous", "NONE");
    System.out.printf("[%s | %s] %s%n", user.username(), user.role(), msg);
  }

  record User(String username, String role) {

  }
}
