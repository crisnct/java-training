
package com.example.training.lambda;

public class LambdaDemo1 {

    public static void main(String[] args) {
        // Before Java 8
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Running (old style)...");
            }
        };

        // With lambda
        Runnable r2 = () -> System.out.println("Running (lambda)...");

        r1.run();
        r2.run();
    }
}
