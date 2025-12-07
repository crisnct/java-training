package com.example.training;

public non-sealed class DefaultGreetingService implements GreetingService {

    @Override
    public String greet(String name) {
        String target;
        if (name == null) {
            target = "World";
        } else {
            String trimmed = name.trim();
            target = trimmed.isEmpty() ? "World" : trimmed;
        }
        return "Hello, " + target + "!";
    }
}
