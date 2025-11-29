package com.example.training;

public class DefaultAndStaticDemo {

  public static void main(String[] args) {
    Greeter greeter = new EnglishGreeter();

    System.out.println(greeter.sayHello());         // default method
    System.out.println(Greeter.createGreeting());   // static method
  }

  interface Greeter {

    // Default method (new in Java 8)
    default String sayHello() {
      return "Hello from default method!";
    }

    /**
     * Static method (also new in Java 8)
     * Before Java 8, utility behavior around an interface had to be dumped into:
     * a random Utils class
     * or a helper class with no real meaning
     * With Java 8 you can group related functionality with the interface:
     */
    static String createGreeting() {
      return "Hello from static interface method!";
    }
  }

  static class EnglishGreeter implements Greeter {
    // No need to override sayHello(), but we can if we want
  }
}
