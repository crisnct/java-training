//@formatter:off
/**
 * Demonstrates Java 25's "Flexible Constructor Bodies" feature.
 * You can now perform setup or validation *before* calling super().
 */
//@formatter:on
package com.example.training;

import java.util.Objects;

class Config {

  private final String dbUrl;
  private final String user;

  Config(String dbUrl, String user) {
    this.dbUrl = dbUrl;
    this.user = user;
  }

  @Override
  public String toString() {
    return "Config[dbUrl=%s, user=%s]".formatted(dbUrl, user);
  }
}

class BaseService {

  private final Config config;

  BaseService(Config config) {
    this.config = config;
    System.out.println("BaseService initialized with " + config);
  }
}

public class DatabaseService extends BaseService {

  private final boolean connected;

  // 🆕 Java 25 allows initialization or validation before calling super()
  public DatabaseService(String dbUrl, String user) {
    // Pre-initialization logic (not allowed before Java 25)
    Objects.requireNonNull(dbUrl, "Database URL cannot be null");
    Objects.requireNonNull(user, "User cannot be null");

    Config config = new Config(dbUrl.trim(), user.trim().toUpperCase());

    // Now we can safely call the superclass constructor
    super(config);

    // Regular body continues as usual
    this.connected = connectToDatabase(config);
    System.out.println("DatabaseService fully initialized ✅");
  }

  private boolean connectToDatabase(Config cfg) {
    System.out.println("Connecting to DB for user " + cfg);
    return true;
  }

  static void main() {
    new DatabaseService("jdbc:mysql://localhost:3306/appdb", "admin");
  }
}
