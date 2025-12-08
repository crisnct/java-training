package com.example.training;

import java.io.PrintStream;

public class GreetingPrinter {

  private final GreetingService greetingService;

  private final PrintStream output;

  public GreetingPrinter(GreetingService greetingService, PrintStream output) {
    if (greetingService == null) {
      throw new NullPointerException("greetingService");
    }
    if (output == null) {
      throw new NullPointerException("output");
    }
    this.greetingService = greetingService;
    this.output = output;
  }

  public void printGreeting(String name) {
    String message = greetingService.greet(name);
    output.println(message);
  }
}
