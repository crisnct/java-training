package com.example.training;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParamNames {

  public static void main(String[] args) throws Exception {
    Method m = ParamNames.class.getDeclaredMethod("pay", String.class, int.class);
    for (Parameter p : m.getParameters()) {
      System.out.println(p.getName() + " : " + p.getType().getSimpleName());
    }
  }

  static void pay(String accountId, int cents) {

  }
}
