package com.example.training;

public class VersionPrinter {

  public String getRuntimeMessage() {
    return "Base implementation (compatible with Java 8)";
  }

  public static void main(String[] args) {
    VersionPrinter printer = new VersionPrinter();
    System.out.println(printer.getRuntimeMessage());
  }
}
