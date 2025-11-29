package com.example.training;

import java.util.*;

public class TypeInferenceDemo {

    public static void main(String[] args) {
        // Compiler infers types better in Java 8
        List<String> list = pick("a", "b");
        System.out.println(list);
    }

    static <T> List<T> pick(T a, T b) {
        List<T> result = new ArrayList<>();
        result.add(a);
        result.add(b);
        return result;
    }
}
