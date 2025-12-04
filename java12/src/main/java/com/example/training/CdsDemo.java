package com.example.training;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * • The JDK build now generates a default CDS archive as part of the build pipeline. • That archive ships inside the JDK image on 64-bit builds. •
 * Combined with the move towards -Xshare:auto as the default behavior, the VM will automatically use CDS when possible. Run with argument
 * -Xlog:cds=info With -Xshare:auto as effective default, the VM automatically tries to map and use it. With -Xshare:off cds will be disabled
 */
public class CdsDemo {

  public static void main(String[] args) {
    System.out.println("App is starting...");
    long start = System.nanoTime();

    // Simulate a small "real" app doing some class loading
    for (int i = 0; i < 50_000_000; i++) {
      String value = "value-" + i;
      String upper = value.toUpperCase();
      if (upper.hashCode() == 0) {
        System.out.println("Impossible, but keeps JIT honest: " + upper);
      }
    }

    // Simulate framework-style startup: load lots of classes, build caches, etc.
    warmUpReflection("java.util");
    warmUpReflection("java.lang");
    warmUpReflection("java.io");
    warmUpCollections();
    warmUpRegex();

    long end = System.nanoTime();
    System.out.printf("App finished in %.2f ms%n", (end - start) / 1_000_000.0);
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
