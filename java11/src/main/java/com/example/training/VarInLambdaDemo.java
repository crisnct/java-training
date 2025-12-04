package com.example.training;

import java.util.function.Function;

public class VarInLambdaDemo {

  public static void main(String[] args) {
    // Using var in lambda parameters
    Function<String, String> formatter =
        (var text) -> text.trim().toUpperCase();

    System.out.println(formatter.apply("  hello java 11  "));
  }
}
