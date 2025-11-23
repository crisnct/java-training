package com.example.training;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy {

  interface Greeter {
    String hi(String name);
  }

  static class LogHandler implements InvocationHandler {

    private final Object target;

    LogHandler(Object t) {
      this.target = t;
    }

    public Object invoke(Object p, Method m, Object[] a) throws Throwable {
      System.out.println(">> " + m.getName());
      return m.invoke(target, a);
    }
  }

  static class GreeterImpl implements Greeter {

    public String hi(String n) {
      return "Hi, " + n;
    }
  }

  public static void main(String[] args) {
    Greeter g = (Greeter) Proxy.newProxyInstance(
        Greeter.class.getClassLoader(),
        new Class[]{Greeter.class},
        new LogHandler(new GreeterImpl())
    );
    System.out.println(g.hi("Cristian"));
  }

}
