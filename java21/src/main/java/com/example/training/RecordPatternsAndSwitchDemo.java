package com.example.training;

import java.util.ArrayList;
import java.util.List;

///@formatter:off
/**
 * Java 21 – Record Patterns (JEP 440)
 *
 * What this demonstrates:
 * 1) instanceof + record pattern: direct, null-safe deconstruction without manual casts.
 * 2) switch over a sealed hierarchy with record patterns + guards (when ...).
 * 3) Nested deconstruction (e.g., extract fields from an Address inside a Customer).
 *
 * Why it’s useful:
 * - Business logic stays declarative and type-safe (no instanceof chains + casts).
 * - Sealed + record patterns make switches exhaustive and refactor-friendly.
 */
//@formatter:on
public class RecordPatternsAndSwitchDemo {

  // ----- Domain -----
  sealed interface Customer permits Individual, Business {

  }

  record Address(String city, String country) {

  }

  record Individual(String fullName, Address address) implements Customer {

  }

  record Business(String legalName, String vatNumber, Address address) implements Customer {

  }

  public static void main(String[] args) {
    List<Customer> customers = new ArrayList<>();
    customers.add(new Individual("Alice Pop", new Address("Cluj-Napoca", "RO")));
    customers.add(new Business("Acme GmbH", "DE123456789", new Address("Berlin", "DE")));
    customers.add(new Business("Soft SRL", "RO987654321", new Address("Timișoara", "RO")));
    customers.add(new Individual("John Smith", new Address("London", "UK")));

    // 1) instanceof + record pattern: pull data without casts
    for (Customer customer : customers) {
      explainCustomer(customer);
    }

    System.out.println("-----");

    // 2) switch + record patterns: routing and tax in one place
    for (Customer customer : customers) {
      String route = computeRoute(customer);
      String tax = computeTaxRule(customer);
      System.out.println(summaryLine(customer) + " | route=" + route + " | tax=" + tax);
    }
  }

  // ----- Example 1: instanceof with record pattern -----
  private static void explainCustomer(Customer customer) {
    // Deconstruct different shapes with a single if/else set.
    if (customer instanceof Individual(String fullName, Address(String city, String country))) {
      System.out.println("Individual: " + fullName + " from " + city + ", " + country);
    } else if (customer instanceof Business biz) {
      System.out.println(
          "Business: " + biz.legalName() + " (VAT " + biz.vatNumber() + ") at " + biz.address().city() + ", " + biz.address().country());
    } else {
      System.out.println("Unknown customer shape");
    }
  }

  // ----- Example 2: switch with record patterns and guards -----

  // Shipping decision based on country and customer type.
  private static String computeRoute(Customer customer) {
    return switch (customer) {
      // Domestic individuals
      case Individual(String name, Address(String city, String country))
          when "RO".equals(country) -> "Domestic: " + city;

      // Domestic businesses (Romanian VAT starts with "RO")
      case Business(String legal, String vat, Address addr)
          when vat != null && vat.startsWith("RO") -> "Domestic-B2B: " + addr.city();

      // Everything else is international
      case Individual(String name, Address addr) -> "International-IND: " + addr.country();

      case Business(String legal, String vat, Address addr) -> "International-B2B: " + addr.country();
    };
  }

  // Tax rule: simple example combining structure + guard.
  private static String computeTaxRule(Customer customer) {
    return switch (customer) {
      case Business(String legal, String vat, Address(String city, String country))
          when vat != null && vat.startsWith("RO") -> "VAT: Reverse-charge eligible (domestic B2B)";

      case Business(String legal, String vat, Address addr) -> "VAT: Export rules (foreign B2B)";

      case Individual(String name, Address(String city, String country)) when "RO".equals(country) -> "VAT: Standard domestic rate";

      case Individual(String name, Address addr) -> "VAT: Destination-country consumer rules";
    };
  }

  // Utility: stable, readable line for printing
  private static String summaryLine(Customer c) {
    if (c instanceof Individual(String fullName, Address a)) {
      return "Individual[" + fullName + ", " + a.city() + ", " + a.country() + "]";
    }
    if (c instanceof Business(String legal, String vat, Address a)) {
      return "Business[" + legal + ", VAT=" + vat + ", " + a.city() + ", " + a.country() + "]";
    }
    return c.toString();
  }
}
