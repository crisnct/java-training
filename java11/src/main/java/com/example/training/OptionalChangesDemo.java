package com.example.training;

import java.util.Optional;

public class OptionalChangesDemo {
    public static void main(String[] args) {
        Optional<String> value = Optional.empty();

        if (value.isEmpty()) {
            System.out.println("Optional is empty.");
        }
    }
}
