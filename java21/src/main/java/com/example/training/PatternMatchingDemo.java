package com.example.training;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

//@formatter:off
/**
 * Java 21 – Pattern Matching for switch (JEP 441)
 *
 * Real use-case: normalize heterogeneous config values to a Duration,
 * then route commands using a sealed hierarchy with type patterns and guards.
 *
 * Highlights:
 * - Type patterns with guards (e.g., Integer i when i >= 0)
 * - Constant/null patterns (handles null directly)
 * - Nested use in a second switch over a sealed type
 */
//@formatter:on
public class PatternMatchingDemo {

  public static void main(String[] args) {
    // 1) Normalize various "timeout" representations to Duration
    List<Object> samples = new ArrayList<>();
    samples.add(500);               // seconds
    samples.add(1500L);             // milliseconds
    samples.add("2s");
    samples.add("250ms");
    samples.add(Duration.ofSeconds(7));
    samples.add(null);

    for (Object v : samples) {
      Duration d = normalizeTimeout(v, Duration.ofSeconds(3));
      System.out.println("in=" + v + " -> " + d);
    }

    System.out.println("-----");

    // 2) Route commands using pattern switch
    List<Command> commands = new ArrayList<>();
    commands.add(new Sleep(normalizeTimeout("750ms", Duration.ofMillis(100))));
    commands.add(new HttpGet(URI.create("https://example.com"), null));
    commands.add(new HttpGet(URI.create("https://openjdk.org"), Duration.ofSeconds(1)));

    for (Command c : commands) {
      System.out.println(dispatch(c));
    }
  }

  /**
   * Normalize arbitrary timeout inputs to a Duration using pattern matching for switch. Accepted forms: - Integer => seconds - Long    =>
   * milliseconds - String  => "123ms" or "5s" - Duration - null => defaultTimeout
   */
  private static Duration normalizeTimeout(Object value, Duration defaultTimeout) {
    return switch (value) {
      case null -> defaultTimeout;

      // Primitive wrappers by type
      case Integer i when i >= 0 -> Duration.ofSeconds(i);
      case Long ms when ms >= 0L -> Duration.ofMillis(ms);

      // Strings with units
      case String s when endsWithIgnoreCase(s, "ms") && isNonNegativeLong(s, 2) -> Duration.ofMillis(parseLongSuffix(s, 2));
      case String s when endsWithIgnoreCase(s, "s") && isNonNegativeLong(s, 1) -> Duration.ofSeconds(parseLongSuffix(s, 1));

      // Already a Duration
      case Duration d when !d.isNegative() -> d;

      // Anything else is invalid
      default -> throw new IllegalArgumentException("Unsupported timeout: " + value);
    };
  }

  // ----- Sealed hierarchy for routing -----
  sealed interface Command permits Sleep, HttpGet {

  }

  record Sleep(Duration duration) implements Command {

  }

  record HttpGet(URI url, Duration timeout) implements Command {

  }

  /**
   * Route commands with a single pattern switch. - Sleep: use provided duration - HttpGet with null timeout: fallback to 500ms - HttpGet with
   * provided timeout: use it
   */
  private static String dispatch(Command cmd) {
    return switch (cmd) {
      case Sleep(Duration d) -> "Sleeping for " + d.toMillis() + " ms";
      // HttpGet with null timeout
      case HttpGet(URI url, Duration t) when t == null -> "GET " + url + " with timeout 500 ms";
      // HttpGet with provided timeout
      case HttpGet(URI url, Duration t) -> "GET " + url + " with timeout " + t.toMillis() + " ms";
    };
  }

  // ----- helpers -----
  private static boolean endsWithIgnoreCase(String s, String suf) {
    int n = s.length(), m = suf.length();
    if (n < m) {
      return false;
    }
    return s.regionMatches(true, n - m, suf, 0, m);
  }

  private static boolean isNonNegativeLong(String s, int unitLen) {
    try {
      long v = Long.parseLong(s.substring(0, s.length() - unitLen));
      return v >= 0L;
    } catch (Exception e) {
      return false;
    }
  }

  private static long parseLongSuffix(String s, int unitLen) {
    return Long.parseLong(s.substring(0, s.length() - unitLen));
  }
}
