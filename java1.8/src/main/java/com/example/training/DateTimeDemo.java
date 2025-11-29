package com.example.training;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeDemo {

  private static final DateTimeFormatter EN_FORMATER
      = DateTimeFormatter.ofPattern("yyyy MMMM dd, HH:mm", Locale.ENGLISH);

  public static void main(String[] args) {
    // ---- LocalDate ----
    LocalDate today = LocalDate.now();
    LocalDate birthDate = LocalDate.of(1991, 2, 25);
    System.out.println("Today: " + today);
    System.out.println("Birth date: " + birthDate);

    // ---- LocalTime ----
    LocalTime nowTime = LocalTime.now();
    LocalTime specificTime = LocalTime.of(9, 30);
    System.out.println("Current time: " + nowTime);
    System.out.println("Specific time: " + specificTime);

    // ---- LocalDateTime ----
    LocalDateTime currentDateTime = LocalDateTime.now();
    LocalDateTime meeting = LocalDateTime.of(2025, 1, 10, 14, 0);
    System.out.println("Current date/time: " + currentDateTime);
    LocalDateTime timeInEngland = LocalDateTime.now(ZoneId.of("Europe/London"));
    System.out.println("Current date/time in England: " + timeInEngland.format(EN_FORMATER));
    System.out.println("Meeting: " + meeting);

    // ---- ZonedDateTime ----
    ZonedDateTime nowInBucharest = ZonedDateTime.now(ZoneId.of("Europe/Bucharest"));
    ZonedDateTime nowInTokyo = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
    System.out.println("Bucharest time: " + nowInBucharest);
    System.out.println("Tokyo time:     " + nowInTokyo);

    // ---- Duration ----
    Duration difference = Duration.between(specificTime, nowTime);
    System.out.println("Duration between times: " + difference.toMinutes() + " minutes");

  }
}
