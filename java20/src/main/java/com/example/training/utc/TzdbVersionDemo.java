package com.example.training.utc;

import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.NavigableMap;

//@formatter:off
/**
 * Shows the installed TZDB version in the current JDK.
 *
 * In Java 20   -> expected: "TZDB 2022g"
 * In Java 20.0.2 -> expected: "TZDB 2023c"
 */
//@formatter:on
public class TzdbVersionDemo {

  public static void main(String[] args) {
    NavigableMap<String, ZoneRules> versions = ZoneRulesProvider.getVersions("UTC");
    versions.forEach((provider, version) ->
        System.out.println(provider + " = " + version));
  }
}
