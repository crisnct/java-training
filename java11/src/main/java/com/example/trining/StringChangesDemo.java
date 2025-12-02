package com.example.trining;

public class StringChangesDemo {

  public static void main(String[] args) {
    String text = "  Hello\nWorld  ";

    // isBlank
    System.out.println("  \t ".isBlank());   // true

    // lines()
    text.lines().forEach(System.out::println);

    // strip, stripLeading, stripTrailing
    System.out.println("strip>" + text.strip() + "<");
    System.out.println("stripLeading>" + text.stripLeading() + "<");
    System.out.println("stripTrailing>" + text.stripTrailing() + "<");

    // repeat
    System.out.println("Hi ".repeat(3));      // Hi Hi Hi
  }
}
