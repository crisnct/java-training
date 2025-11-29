package com.example.training;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class JoinDemo {

  public static void main(String[] args) {
    List<String> items = Arrays.asList("alpha", "beta", "gamma");

    StringJoiner sj = new StringJoiner(", ", "[", "]");
    items.forEach(sj::add);
    System.out.println(sj);           // [alpha, beta, gamma]

    String csv = String.join(",", items);        // simpler one-liner
    System.out.println(csv);
  }
}
