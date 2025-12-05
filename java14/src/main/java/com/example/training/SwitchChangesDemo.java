package com.example.training;

import java.time.Month;
import java.time.Year;

public class SwitchChangesDemo {

  public static void main(String[] args) {
    int year = 2020;

    int days = daysInMonth(year, Month.FEBRUARY);        // uses -> and yield
    String klass = httpClass(201);                       // pure -> expression
    int sign = signOf(0);                                // multi-label

    System.out.println("Days in FEB " + year + ": " + days);
    System.out.println("HTTP 201 class: " + klass);
    System.out.println("Sign of 0: " + sign);
  }

  // Example 1: compute result with logic, then yield it
  private static int daysInMonth(int year, Month month) {
    return switch (month) {
      case APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30;
      case JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER -> 31;
      case FEBRUARY -> {
        boolean leap = Year.isLeap(year);
        int value = leap ? 29 : 28;
        yield value; // required when using a block
      }
    };
  }

  // Example 2: concise mapping using arrow labels
  private static String httpClass(int status) {
    int hundred = status / 100;
    return switch (hundred) {
      case 1 -> "Informational";
      case 2 -> "Success";
      case 3 -> "Redirection";
      case 4 -> "Client Error";
      case 5 -> "Server Error";
      default -> "Unknown";
    };
  }

  // Example 3: multi-label, switch as an expression returning a value
  private static int signOf(int value) {
    return switch (value) {
      case -1 -> -1;
      case 0 -> 0;
      case 1 -> {
        int x = 2;
        yield x + 12;
      }
      case 2 -> 20;
      default -> {
        // compress any other integer to -1 or 1
        yield value < 0 ? -1 : 1;
      }
    };
  }

  private String getQuarter(String month) {
    return switch (month) {
      case "JANUARY", "FEBRUARY", "MARCH" -> "Q1";
      case "APRIL", "MAY", "JUNE" -> "Q2";
      case "JULY", "AUGUST", "SEPTEMBER" -> "Q3";
      case "OCTOBER", "NOVEMBER", "DECEMBER" -> "Q4";
      default -> throw new IllegalArgumentException("Invalid month: " + month);
    };
  }

}
