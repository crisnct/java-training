package com.example.training;

/**
 * Run with argument -XX:+ShowCodeDetailsInExceptionMessages From java 15 there is no need to set the argument
 */
public class HelpfulNpeDemo {

  static class Address {

    String city;
  }

  static class Person {

    Address address;
  }

  public static void main(String[] args) {
    Person person = new Person(); // address is null

    // This will trigger a NullPointerException
    System.out.println(person.address.city);
  }
}
