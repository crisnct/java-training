package com.example.training;

class Nestmate {
  private String secret = "Private field in outer class";

  class Inner {

    private String innerSecret = "Private field in inner class";

    void printOuterSecret() {
      // In Java 11, this direct access uses nest-based access control
      System.out.println(secret);

      // Outer class can also access my private fields:
      System.out.println(getInnerSecret());
    }

    private String getInnerSecret() {
      return innerSecret;
    }
  }
}
