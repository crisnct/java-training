package com.example.training.lambda;

public class LambdaDemo3 {

  public static void main(String[] args) {
    Operation sum = (a, b) -> a + b;

    int result = sum.apply(5, 7);
    System.out.println("Result = " + result);
  }

  @FunctionalInterface
  interface Operation {
    int apply(int x, int y);
  }
}
