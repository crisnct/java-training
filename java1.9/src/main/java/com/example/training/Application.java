package com.example.training;

import com.example.training.api.GreetingService;
import com.example.training.api.LisaGreetingService;

public class Application {

  public static void main(String[] args) {
    GreetingService g = new DefaultGreetingService();
    System.out.println(g.greet("Cristian"));

    g = new LisaGreetingService();
    System.out.println(g.greet("Cristian"));
  }
}
