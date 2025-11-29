package com.example.training.functionalInterfaces;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class SupplierFactoryDemo {

  public static void main(String[] args) {
    Supplier<User> userFactory = () -> new User("Cristian", 43);

    User u = userFactory.get();
    System.out.println(u);

    LongSupplier timestampSupplier = System::currentTimeMillis;
    System.out.println("timestamp: " + timestampSupplier.getAsLong());
  }

  static class User {

    private final String name;
    private final int age;

    User(String name, int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public String toString() {
      return name + " (" + age + ")";
    }
  }
}
