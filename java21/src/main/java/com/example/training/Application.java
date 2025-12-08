package com.example.training;

//@formatter:off
/**
 * Create jmod archive file
 * Execute this command in target/classes folder:
 * "C:\.....\temurin-25\bin\jmod.exe" create ^
 *   --class-path . ^
 *   --compress zip-9 ^
 *   "java20-demo.jmod"
 *   It will create java20-demo.jmod with best compression (9). The project must be modularized.
 */
//@formatter:on
public class Application {

  public static void main(String[] args) {
    GreetingService greetingService = new DefaultGreetingService();
    GreetingPrinter printer = new GreetingPrinter(greetingService, System.out);
    String name = args.length > 0 ? args[0] : "World";
    printer.printGreeting(name);
  }
}
