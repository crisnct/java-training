package com.example.training;

public class TextBlockExample {

  public static void main(String[] args) {
    String json = """
        {
          "name": "Cristian",
          "role": "Developer"
        }
        """;

    // Normal String usage
    System.out.println("Raw JSON:");
    System.out.println(json);

    // Text blocks preserve line breaks and formatting
    System.out.println("Length = " + json.length());
    System.out.println("Contains role? " + json.contains("Developer"));
  }
}
