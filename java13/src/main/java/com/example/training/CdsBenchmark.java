package com.example.training;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * <br>How to generate jsa file:</br>
 * <br>java -XX:ArchiveClassesAtExit=cdsdemo.jsa -Xlog:cds=info -jar java13-1.0.0.jar</br>
 * <br>How to use jsa file: </br>
 * <br>java -Xshare:on -XX:SharedArchiveFile=cdsdemo.jsa -Xlog:cds=info -jar java13-1.0.0.jar</br>
 */
public class CdsBenchmark {

  static {
    preloadThirdParty();
    preloadOwnClasses();
  }

  public static void main(String[] args) {
    long t0 = System.nanoTime();

    // JDK replacements for Guava utilities
    Map<String, Integer> map = Map.of("a", 1, "b", 2, "c", 3);
    String random = RandomStringUtils.randomAlphanumeric(2048);

    String joined = joinFixedChunks(random, 8, '|');
    String encoded = Base64.getEncoder().encodeToString(joined.getBytes(StandardCharsets.UTF_8));
    byte[] hash = sha256(encoded.getBytes(StandardCharsets.UTF_8));

    // Jackson touch
    ObjectMapper om = new ObjectMapper(new JsonFactory());
    Map<?, ?> parsed;
    try {
      parsed = om.readValue("{\"k\":1,\"v\":[1,2,3]}", Map.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    long t1 = System.nanoTime();

    System.out.println("Args: " + ManagementFactory.getRuntimeMXBean().getInputArguments());
    System.out.println("Hash[0] = " + (hash.length > 0 ? (hash[0] & 0xFF) : -1));
    System.out.println("Parsed size = " + parsed.size());
    System.out.println("Startup+init time = " + Duration.ofNanos(t1 - t0).toMillis() + " ms");
  }

  private static void preloadThirdParty() {
    // Touch commonly used classes so they load at startup (eligible for CDS)
    Class<?>[] types = new Class<?>[]{
        ObjectMapper.class, JsonFactory.class,
        StringUtils.class, RandomStringUtils.class
    };
    for (Class<?> c : types) {
      touch(c);
    }
  }

  private static void preloadOwnClasses() {
    List<Class<?>> own = new ArrayList<>();
    own.add(T0.class);
    own.add(T1.class);
    own.add(T2.class);
    own.add(T3.class);
    own.add(T4.class);
    own.add(T5.class);
    own.add(T6.class);
    own.add(T7.class);
    own.add(T8.class);
    own.add(T9.class);
    own.add(T10.class);
    own.add(T11.class);
    own.add(T12.class);
    own.add(T13.class);
    own.add(T14.class);
    own.add(T15.class);
    own.add(T16.class);
    own.add(T17.class);
    own.add(T18.class);
    own.add(T19.class);
    own.add(T20.class);
    own.add(T21.class);
    own.add(T22.class);
    own.add(T23.class);
    own.add(T24.class);
    own.add(T25.class);
    own.add(T26.class);
    own.add(T27.class);
    own.add(T28.class);
    own.add(T29.class);
    for (Class<?> c : own) {
      touch(c);
    }
  }

  private static void touch(Class<?> c) {
    try {
      Class.forName(c.getName(), true, CdsBenchmark.class.getClassLoader());
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  // ---- Helpers (JDK equivalents for former Guava calls)

  private static String joinFixedChunks(String s, int chunkSize, char separator) {
    if (chunkSize <= 0) {
      return s;
    }
    StringBuilder sb = new StringBuilder(s.length() + s.length() / chunkSize);
    int i = 0;
    while (i < s.length()) {
      int end = Math.min(i + chunkSize, s.length());
      sb.append(s, i, end);
      i = end;
      if (i < s.length()) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }

  private static byte[] sha256(byte[] data) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      return md.digest(data);
    } catch (Exception e) {
      throw new IllegalStateException("SHA-256 unavailable", e);
    }
  }


  public static final class T0 {

    static final int x = 0;
  }

  public static final class T1 {

    static final int x = 1;
  }

  public static final class T2 {

    static final int x = 2;
  }

  public static final class T3 {

    static final int x = 3;
  }

  public static final class T4 {

    static final int x = 4;
  }

  public static final class T5 {

    static final int x = 5;
  }

  public static final class T6 {

    static final int x = 6;
  }

  public static final class T7 {

    static final int x = 7;
  }

  public static final class T8 {

    static final int x = 8;
  }

  public static final class T9 {

    static final int x = 9;
  }

  public static final class T10 {

    static final int x = 10;
  }

  public static final class T11 {

    static final int x = 11;
  }

  public static final class T12 {

    static final int x = 12;
  }

  public static final class T13 {

    static final int x = 13;
  }

  public static final class T14 {

    static final int x = 14;
  }

  public static final class T15 {

    static final int x = 15;
  }

  public static final class T16 {

    static final int x = 16;
  }

  public static final class T17 {

    static final int x = 17;
  }

  public static final class T18 {

    static final int x = 18;
  }

  public static final class T19 {

    static final int x = 19;
  }

  public static final class T20 {

    static final int x = 20;
  }

  public static final class T21 {

    static final int x = 21;
  }

  public static final class T22 {

    static final int x = 22;
  }

  public static final class T23 {

    static final int x = 23;
  }

  public static final class T24 {

    static final int x = 24;
  }

  public static final class T25 {

    static final int x = 25;
  }

  public static final class T26 {

    static final int x = 26;
  }

  public static final class T27 {

    static final int x = 27;
  }

  public static final class T28 {

    static final int x = 28;
  }

  public static final class T29 {

    static final int x = 29;
  }
}
