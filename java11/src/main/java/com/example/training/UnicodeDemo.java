package com.example.training;

public class UnicodeDemo {

  public static void main(String[] args) {
    String emoji = "♥🧟"; // Unicode 10 zombie emoji

    System.out.println(emoji.length());          // 2 (UTF-16 units)
    System.out.println(emoji.codePointCount(0, emoji.length())); // 1 code point

    System.out.println(Character.getName(emoji.codePointAt(0))); // Prints official Unicode name
    System.out.println(Character.getName(emoji.codePointAt(1))); // Prints official Unicode name
  }
}
