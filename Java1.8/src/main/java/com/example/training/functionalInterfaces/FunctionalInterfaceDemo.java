package com.example.training.functionalInterfaces;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FunctionalInterfaceDemo {

  public static void main(String[] args) {
    // Lambda for addition
    Calculator add = Integer::sum;

    // Lambda for multiplication
    Calculator multiply = (a, b) -> a * b;

    int x = 5;
    int y = 3;

    System.out.println("Add = " + add.compute(x, y));
    System.out.println("Multiply = " + multiply.compute(x, y));

    // Function<T, R>
    Function<String, Integer> lengthFunc = String::length;
    System.out.println("length(\"lambda\"): " + lengthFunc.apply("lambda"));

    // Consumer<T>
    Consumer<String> printer = s -> System.out.println("Consumed: " + s);
    printer.accept("Hello from Consumer");

    // UnaryOperator<T>
    UnaryOperator<Integer> doubleIt = w -> w * 2;
    System.out.println("doubleIt(10): " + doubleIt.apply(10));

    // BinaryOperator<T>
    BinaryOperator<Integer> add2 = (i, j) -> i + j;
    System.out.println("add2(3, 4): " + add2.apply(3, 4));
  }

  @FunctionalInterface
  interface Calculator {

    int compute(int a, int b);

    default int computeProduct(int a, int b) {
      return a * b;
    }

    default int computeDifference(int a, int b) {
      return a - b;
    }
  }

}
