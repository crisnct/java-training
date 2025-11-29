package com.example.training.lambda;

import java.util.*;

public class LambdaDemo2 {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Ana", "Cristian", "Bob");

        // Before Java 8
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        });

        System.out.println("Sorted (old): " + names);

        // Lambda version
        names.sort((a, b) -> a.length() - b.length());

        System.out.println("Sorted (lambda): " + names);
    }
}
