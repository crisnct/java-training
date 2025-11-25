package com.example.training;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CollectionsDemo {

    public static void main(String[] args) {
        List<String> names = new ArrayList<String>();
        names.add("Charlie");
        names.add("Bob");
        names.add("Alice");

        Collections.sort(names);
        System.out.println("Sorted names: " + names);

        int indexOfBob = Collections.binarySearch(names, "Bob");
        System.out.println("Index of 'Bob': " + indexOfBob);

        List<String> unmodifiableNames = Collections.unmodifiableList(names);
        System.out.println("Unmodifiable view: " + unmodifiableNames);
    }
}
