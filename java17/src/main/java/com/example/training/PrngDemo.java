package com.example.training;
//@formatter:off
/*
 * Java 17 example: Enhanced pseudo-random number generators (JEP 356)
 * - Enhanced pseudo-random number generators
 * - JEP 356 – Enhanced Pseudo-Random Number Generators
 * - New java.util.random package:
 * - pluggable PRNGs,
 * - jumpable, splitable generators,
 * - better separation between algorithms and API.
 * - RandomGenerator is the common interface for all PRNGs
 * - RandomGenerator.getDefault() gives the recommended default generator
 * - RandomGeneratorFactory lets you choose a specific algorithm by name
 * - You can list all available PRNG algorithms at runtime (pluggable model)
 */
//@formatter:on

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class PrngDemo {

  public static void main(String[] args) {
    useDefaultGenerator();
    useJumpableGenerator();
    useSplittableGenerator();
  }

  private static void useDefaultGenerator() {
    RandomGenerator rng = RandomGenerator.getDefault();

    System.out.println("--- Default generator ---");
    System.out.println("Type        : " + rng.getClass().getName());
    System.out.println("nextInt()   : " + rng.nextInt());
    System.out.println("nextDouble(): " + rng.nextDouble());
    System.out.println();
  }

  private static void useJumpableGenerator() {
    // Xoshiro256PlusPlus implements RandomGenerator.JumpableGenerator in Java 17
    RandomGenerator base =
        RandomGeneratorFactory.of("Xoshiro256PlusPlus").create(42L);

    System.out.println("--- Jumpable generator ---");
    if (!(base instanceof RandomGenerator.JumpableGenerator jumpable)) {
      System.out.println("Algorithm is not jumpable");
      System.out.println();
      return;
    }

    // copy() returns another generator at the same position in the sequence
    RandomGenerator.JumpableGenerator copy = jumpable.copy();

    System.out.println("Before jump()");
    System.out.println("original nextInt(): " + jumpable.nextInt());
    System.out.println("copy     nextInt(): " + copy.nextInt());

    // jump() moves the original far ahead in its state cycle

    jumpable.jump();

    System.out.println("After jump()");
    System.out.println("original nextInt(): " + jumpable.nextInt());
    System.out.println("copy     nextInt(): " + copy.nextInt());
    System.out.println();
  }

  private static void useSplittableGenerator() {
    // L64X256MixRandom implements RandomGenerator.SplittableGenerator in Java 17
    RandomGenerator base =
        RandomGeneratorFactory.of("L64X256MixRandom").create(123L);

    System.out.println("--- Splittable generator ---");
    if (!(base instanceof RandomGenerator.SplittableGenerator splittable)) {
      System.out.println("Algorithm is not splittable");
      System.out.println();
      return;
    }

    // split() creates independent generators, ideal for different threads/tasks
    RandomGenerator.SplittableGenerator worker1 = splittable.split();
    RandomGenerator.SplittableGenerator worker2 = splittable.split();

    System.out.println("worker1 nextInt(): " + worker1.nextInt());
    System.out.println("worker1 nextInt(): " + worker1.nextInt());

    System.out.println("worker2 nextInt(): " + worker2.nextInt());
    System.out.println("worker2 nextInt(): " + worker2.nextInt());
    System.out.println();
  }
}
