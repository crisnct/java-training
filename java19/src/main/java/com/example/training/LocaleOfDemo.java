package com.example.training;

import java.util.Locale;

//@formatter:off
// Demo class to show Java 19 Locale API updates: prefer Locale.of(...) over deprecated constructors
//@formatter:on
public class LocaleOfDemo {

  public static void main(String[] args) {

    // Old style (deprecated in Java 19):
    // Locale localeDeprecated = new Locale("en", "US");

    // New, recommended style:
    Locale englishUS = Locale.of("en", "US");
    Locale germanGermany = Locale.of("de", "DE");
    Locale romanianRomania = Locale.of("ro", "RO");

    System.out.println("English (US):       " + englishUS);
    System.out.println("German  (Germany): " + germanGermany);
    System.out.println("Romanian (Romania): " + romanianRomania);

    // You can also create with variants:
    Locale swissGerman = Locale.of("de", "CH", "1996");
    System.out.println("Swiss German: " + swissGerman);

    // And with just language:
    Locale french = Locale.of("fr");
    System.out.println("French: " + french);
  }
}
