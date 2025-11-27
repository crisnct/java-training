package com.example.training;

public class StringSwitchDemo {

    public static void main(String[] args) {
        String command = "start";

        switch (command) {
            case "start":
                System.out.println("Starting the process...");
                break;

            case "stop":
                System.out.println("Stopping the process...");
                break;

            case "pause":
                System.out.println("Pausing the process...");
                break;

            default:
                System.out.println("Unknown command: " + command);
        }
    }
}
