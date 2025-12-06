package com.example.training;

import java.util.Objects;

//@formatter:off
/**
 * Utility for joining two strings with a dash.
 *
 * <p>Usage example:</p>
 *
 * {@snippet :
 *   String a = "Hello";
 *   String b = "World";
 *   String result = Joiner.join(a, b);
 *   System.out.println(result); // Hello-World
 * }
 *
 * <p>The {@snippet} tag supports:
 * syntax highlighting,
 * region-based extraction,
 * and better formatting than raw <pre> blocks.</p>
 */
//@formatter:on
public final class Joiner {

  private Joiner() {
  }

  /**
   * Joins two non-null strings using a hyphen.
   */
  public static String join(String first, String second) {
    Objects.requireNonNull(first);
    Objects.requireNonNull(second);
    return first + "-" + second;
  }

  public static void main(String[] args) {
    System.out.println(join("Java", "18"));
  }
}
