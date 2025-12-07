package com.example.training;

public sealed interface GreetingService permits DefaultGreetingService{

    String greet(String name);
}
