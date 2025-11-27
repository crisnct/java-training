package com.example.training;

public class UnderscoreLiteralDemo {

    public static void main(String[] args) {
        int oneMillion = 1_000_000;
        long creditCard = 1234_5678_9012_3456L;
        double pi = 3.14_15_92;
        double random = 2_1121_21_2121212_122.3_212_1_222_1d;
        int binaryMask = 0b1111_0000; // Java 7 also added binary literals

        System.out.println("oneMillion = " + oneMillion);
        System.out.println("creditCard = " + creditCard);
        System.out.println("pi = " + pi);
        System.out.println("binaryMask = " + binaryMask);
        System.out.println("random = " + random);
    }
}
