package com.example.training;
//@formatter:off
/*
 Demonstrates Java 18's UTF-8 default charset behavior.

 1. Write a UTF-8 string containing non-ASCII characters to a file,
    without specifying a charset.

 2. Read the file back using FileReader (no charset argument).

 Before Java 18:
    Result depended on OS default encoding (Windows CP-1250, etc.)
    and often corrupted non-ASCII characters.

 In Java 18 and later:
    Always UTF-8. Output is consistent across all systems.
*/
//@formatter:on

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Utf8DefaultDemo {

  public static void main(String[] args) throws IOException {

    File file = new File("test.txt");

    // Write a UTF-8 string (diacritics and emoji)
    String text = "Mănânc mere 🍎 și beau cafea ☕";

    // Before Java 18: risky, platform encoding unknown
    // Java 18: always UTF-8
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(text);
    }

    // Read without specifying charset
    // Before Java 18: often corrupted on Windows
    // Java 18: always UTF-8, correct output
    try (FileReader reader = new FileReader(file)) {
      char[] buffer = new char[200];
      int read = reader.read(buffer);
      System.out.println("Read text: " + new String(buffer, 0, read));
    }
  }
}
