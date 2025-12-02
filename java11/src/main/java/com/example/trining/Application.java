package com.example.trining;

public class Application {

  public static void main(String[] args) {
    GreetingService g = new DefaultGreetingService();
    System.out.println(g.greet("Cristian"));
  }
}
