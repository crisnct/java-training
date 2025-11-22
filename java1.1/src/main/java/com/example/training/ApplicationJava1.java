package com.example.training;

public class ApplicationJava1 {

    public static void main(String[] args) {
        GreetingService greetingService = new DefaultGreetingService();
        GreetingPrinter printer = new GreetingPrinter(greetingService, System.out);
        String name = args.length > 0 ? args[0] : "World";
        printer.printGreeting(name);
        new JavaDataTypes();
    }

}
