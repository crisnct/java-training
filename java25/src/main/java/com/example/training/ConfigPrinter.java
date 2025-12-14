package com.example.training;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Demonstrates Java 25's instance main() methods. Compact source files need
 * the unnamed package, so this class form keeps the module descriptor valid
 * while still using an instance main entrypoint.
 */
public class ConfigPrinter {

  void main() throws Exception {
    var configPath = Path.of("app.properties");

    if (Files.notExists(configPath)) {
      Files.writeString(configPath, """
          db.url=jdbc:mysql://localhost:3306/appdb
          db.user=admin
          db.pass=secret
          """);
      System.out.println("Created default config file.");
    }

    var props = new Properties();
    try (var reader = Files.newBufferedReader(configPath)) {
      props.load(reader);
    }

    System.out.println("\nLoaded Configuration:");
    props.forEach((k, v) -> System.out.println(" - %s = %s".formatted(k, v)));
  }

}
