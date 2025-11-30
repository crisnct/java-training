package com.example.training;

public class VersionPrinter {

  public String getRuntimeMessage() {
    // API specific Java 9+
    Runtime.Version version = Runtime.version();
    return "Java 9+ implementation, runtime version = " + version;
  }

  public static void main(String[] args) {
    VersionPrinter printer = new VersionPrinter();
    System.out.println(printer.getRuntimeMessage());
  }
}
