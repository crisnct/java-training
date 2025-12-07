package com.example.training;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

//@formatter:off
// Demo class to show ISO and localized DateTimeFormatter output with Java 19 locale/CLDR updates
//@formatter:on
public class DateTimeFormatterDemo {

  public static void main(String[] args) {
    ZonedDateTime nowInBucharest = ZonedDateTime.now(ZoneId.of("Europe/Bucharest"));

    // ISO_LOCAL_DATE_TIME works on the local date-time part
    String isoLocal = nowInBucharest
        .toLocalDateTime()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    System.out.println("ISO_LOCAL_DATE_TIME: " + isoLocal);

    // Localized SHORT for Germany (uses updated patterns/CLDR data in Java 19)
    DateTimeFormatter germanShort = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.GERMANY);
    System.out.println("German SHORT: " + germanShort.format(nowInBucharest));

    // Localized FULL for France
    DateTimeFormatter frenchFull = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.FULL)
        .withLocale(Locale.FRANCE);
    System.out.println("French FULL: " + frenchFull.format(nowInBucharest));

    // Localized MEDIUM for Romania
    DateTimeFormatter romanianMedium = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.of("ro"));
    System.out.println("Romanian MEDIUM: " + romanianMedium.format(nowInBucharest));
  }
}
