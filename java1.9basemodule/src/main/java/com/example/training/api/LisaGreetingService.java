package com.example.training.api;

import java.util.Objects;

public class LisaGreetingService implements GreetingService {

  @Override
  public String greet(String name) {
    Objects.requireNonNull(name);
    return "Hello, " + name + "! I'm Lisa";
  }
}
