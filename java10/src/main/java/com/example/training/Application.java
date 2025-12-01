package com.example.training;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Application {

  public static void main(String[] args) {
    GreetingService g = new DefaultGreetingService();
    System.out.println(g.greet("Cristian"));

    long start = System.nanoTime();

    // Simulate framework-style startup: load lots of classes, build caches, etc.
    warmUpReflection("java.util");
    warmUpReflection("java.lang");
    warmUpReflection("java.io");
    warmUpCollections();
    warmUpRegex();

    long end = System.nanoTime();
    long startupMillis = (end - start) / 1_000_000;
    System.out.println("Startup work completed in " + startupMillis + " ms");
  }

  // Force loading of multiple application classes
  private static void warmUpReflection(String packageName) {
    Path modulePath = FileSystems.getFileSystem(URI.create("jrt:/"))
        .getPath("/modules/java.base/" + packageName.replaceAll("\\.", "/"));

    try (Stream<Path> stream = Files.walk(modulePath, 1)) {
      stream.filter(path -> path.toString().endsWith(".class"))
          .forEach(path -> {
            String name = path.getFileName().toString();
            name = name.substring(0, name.length() - 6); // remove .class
            try {
              Class.forName(packageName + "." + name);
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
            System.out.println(name);
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Allocate a bunch of objects and fill some collections
  private static void warmUpCollections() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < 100_000; i++) {
      list.add("value-" + i);
    }
  }

  // Compile some regex patterns (also creates metadata)
  private static void warmUpRegex() {
    for (int i = 0; i < 1_000; i++) {
      Pattern.compile("value-(\\d+)-" + i);
    }
  }
}
