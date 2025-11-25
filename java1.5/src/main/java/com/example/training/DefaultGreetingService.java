package com.example.training;

public class DefaultGreetingService extends AbstractService {

    @Override
    public String greet(String name) {
        String target;
        if (name == null) {
            target = "World";
        } else {
            String trimmed = name.trim();
            target = trimmed.length() == 0 ? "World" : trimmed;
        }
        return "Hello, " + target + "!";
    }
}
