package com.example.training;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomDemo {

    public static void main(String[] args) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Random int in [1, 6]
        int dice = random.nextInt(1, 7);

        // Random double in [0.0, 1.0)
        double probability = random.nextDouble();

        // Random long in [1000, 9999]
        long code = random.nextLong(1000L, 10_000L);

        System.out.println("dice = " + dice);
        System.out.println("probability = " + probability);
        System.out.println("code = " + code);
    }
}
