package com.example.training.utc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesProvider;

//@formatter:off
/**
 * Displays the offset for a zone that changed rules between TZDB versions.
 *
 * Check Palestine (Asia/Gaza) or Fiji or Samoa for recent TZ updates.
 */
//@formatter:on
public class TzRuleCheckDemo {

  public static void main(String[] args) {
    ZoneId zone = ZoneId.of("Europe/Berlin");
    ZonedDateTime now = ZonedDateTime.now(zone);
    System.out.println("Zone: " + zone);
    System.out.println("Offset: " + now.getOffset());

    ZoneId zoneBuc = ZoneId.of("Europe/Bucharest");
    ZonedDateTime nowBuc = ZonedDateTime.now(zoneBuc);
    System.out.println("Zone: " + zoneBuc);
    System.out.println("Offset: " + nowBuc.getOffset());

    System.out.println("Rules version: " +
        ZoneRulesProvider.getVersions("UTC").values());
  }
}
