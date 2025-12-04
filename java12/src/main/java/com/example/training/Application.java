package com.example.training;

public class Application {

  public static void main(String[] args) {
    GreetingService g = new DefaultGreetingService();
    System.out.println(g.greet("Cristian"));
  }
}
