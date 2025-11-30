package com.example.training;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

public class Nio2Demo {

  public static void main(String[] args) throws Exception {
    Nio2Demo demo = new Nio2Demo();
    demo.basicPathAndFiles();
    demo.watchDirectory();
  }

  // -------------------------------------------------------------
  // Path + Files + FileSystem
  // -------------------------------------------------------------
  public void basicPathAndFiles() throws IOException {
    System.out.println("=== Basic Path / Files example ===");

    Path path = Paths.get("sample.txt");

    // Write text
    Files.write(path, "Hello from Java 7 NIO.2".getBytes());

    // Read text
    String content = new String(Files.readAllBytes(path));
    System.out.println("Content: " + content);

    // FileSystem
    FileSystem fs = FileSystems.getDefault();
    System.out.println("Separator: " + fs.getSeparator());

    // Delete file
    Files.deleteIfExists(path);
  }

  // -------------------------------------------------------------
  // WatchService
  // -------------------------------------------------------------
  public void watchDirectory() throws Exception {
    System.out.println("\n=== WatchService example ===");

    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      Path dir = Paths.get(".");
      // Register for CREATE, MODIFY, DELETE events
      dir.register(
          watchService,
          StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_MODIFY,
          StandardWatchEventKinds.ENTRY_DELETE
      );

      System.out.println("Waiting for events... (300 seconds)");
      while (true) {
        WatchKey key = watchService.poll(300, TimeUnit.SECONDS);
        if (key != null) {
          for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            Path fileName = (Path) event.context();
            System.out.println("Event: " + kind + " on " + fileName);
          }
          boolean valid = key.reset();
          if (!valid) {
            System.out.println("WatchKey is no longer valid. Stopping watcher.");
            break;
          }
        } else {
          System.out.println("No events occurred.");
        }
      }
    }
  }
}
