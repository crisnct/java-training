package com.example.training;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@FunctionalInterface
interface Adder {
  int add(int a, int b);
}

public class IndyLambda{

  public static void main(String[] args) throws Throwable {
    MethodHandles.Lookup L = MethodHandles.lookup();
    MethodType func = MethodType.methodType(int.class, int.class, int.class); // (a,b)->int
    MethodHandle target = L.findStatic(IndyLambda.class, "plus", func);

    CallSite cs = LambdaMetafactory.metafactory(
        L,
        "add",                               // SAM name
        MethodType.methodType(Adder.class),  // factory type -> ( ) Adder
        func,                                // erased SAM type
        target,                              // implementation handle
        func                                 // specialized SAM type
    );
    Adder adder = (Adder) cs.getTarget().invokeExact();
    System.out.println(adder.add(3, 4)); // 7
  }

  static int plus(int a, int b) {
    return a + b;
  }
}
