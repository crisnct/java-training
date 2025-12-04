package com.example.training;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileReadWriteDemo {

  public static void main(String[] args) throws Exception {
    Path path = Path.of("test.txt");

    // Write text
    Files.writeString(path, "Hello Java 11\nLine two");

    // Read text
    String content = Files.readString(path);

    System.out.println(content);

    path.toFile().deleteOnExit();
  }
}
