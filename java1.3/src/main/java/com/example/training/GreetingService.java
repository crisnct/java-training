package com.example.training;

/**
 * Utility for greetings.
 * <p>Demonstrates 1.3-era javadoc formatting and tags.</p>
 *
 * @author Cristian Tone
 * @version 1.0
 * @since 1.3
 */
public interface GreetingService {

  /**
   * Returns a greeting message.
   *
   * @param name non-null person name
   * @return greeting text
   * @throws IllegalArgumentException if name is null/empty
   */
  String greet(String name);

}
