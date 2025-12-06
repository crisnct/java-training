package com.example.training;

public class DefaultGreetingService implements GreetingService {

  private final InternalLogic logic = new InternalLogic(); // internal class

  @Override
  public String greet(String name) {
    return logic.format(name);
  }

}
