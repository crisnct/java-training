package com.example.training;

public class NestmateDemo {

    private String secret = "Private field in outer class";

    public static void main(String[] args) {
        NestmateDemo demo = new NestmateDemo();
        Inner inner = demo.new Inner();
        inner.printOuterSecret();
    }

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
