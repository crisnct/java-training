package com.example.training;

import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;

/**
 * JEP 387 – Elastic Metaspace
 *
 * Improves metaspace memory management, returns memory to OS more aggressively.
 * Less footprint and better behavior for apps that load/unload lots of classes.
 */
public class ElasticMetaspaceStreamDemo {

  private static final int LOAD_PHASES = 5;            // de câte ori facem "valuri"
  private static final int CYCLES_PER_LOAD_PHASE = 20; // câte cicluri de load/unload per val
  private static final int CLASSES_PER_CYCLE = 200;

  private static final String TARGET_CLASS_NAME =
      ElasticMetaspaceStreamDemo.class.getName() + "$Target";

  // memorează ultima valoare văzută pentru fiecare câmp numeric din MetaspaceSummary
  private static final Map<String, Long> lastValues = new HashMap<>();

  public static void main(String[] args) throws Exception {
    System.out.println("PID = " + ProcessHandle.current().pid());
    System.out.println("Starting live JFR Streaming (watch for ↑ / ↓ on metaspace fields)...");

    try (RecordingStream stream = new RecordingStream()) {
      stream.enable("jdk.MetaspaceSummary").withPeriod(Duration.ofSeconds(1));

      stream.onEvent("jdk.MetaspaceSummary", ElasticMetaspaceStreamDemo::handleMetaspaceSummary);

      stream.setReuse(true);
      stream.startAsync(); // pornește JFR în thread separat

      runLoadUnloadPhases();

      // fază de cooldown: doar GC, fără load, ca să vezi clar scăderea
      System.out.println("Cooldown: forcing GC without new class loading...");
      for (int i = 0; i < 20; i++) {
        System.gc();
        Thread.sleep(500);
      }

      Thread.sleep(2000); // să apuce stream-ul să mai printeze
    }

    System.out.println("Done. Look above for metaspace fields with ↓ (clear benefit of elastic metaspace).");
  }

  private static void handleMetaspaceSummary(RecordedEvent event) {
    StringBuilder increases = new StringBuilder();
    StringBuilder decreases = new StringBuilder();

    event.getEventType().getFields().forEach(field -> {
      String fieldName = field.getName();
      Object valueObject;

      try {
        valueObject = event.getValue(fieldName);
      } catch (IllegalArgumentException e) {
        return;
      }

      if (!(valueObject instanceof Long)) {
        return;
      }

      long current = (Long) valueObject;
      Long previous = lastValues.put(fieldName, current);
      if (previous == null) {
        return;
      }

      // prag minim ca să nu spammăm pentru fluctuații foarte mici
      long delta = current - previous;
      long threshold = 1024 * 1024; // 1 MB

      if (delta > threshold) {
        increases.append(String.format("  %s ↑ %d -> %d (+%d)%n",
            fieldName, previous, current, delta));
      } else if (delta < -threshold) {
        long absDelta = -delta;
        decreases.append(String.format("  %s ↓ %d -> %d (-%d)%n",
            fieldName, previous, current, absDelta));
      }
    });

    if (!increases.isEmpty() || !decreases.isEmpty()) {
      System.out.println("=== Metaspace change ===");
      if (!increases.isEmpty()) {
        System.out.print(increases);
      }
      if (!decreases.isEmpty()) {
        System.out.print(decreases);
      }
      System.out.println();
    }
  }

  private static void runLoadUnloadPhases() throws Exception {
    for (int phase = 1; phase <= LOAD_PHASES; phase++) {
      System.out.println("=== LOAD PHASE " + phase + " ===");

      for (int cycle = 1; cycle <= CYCLES_PER_LOAD_PHASE; cycle++) {
        loadAndDropClasses();
        System.gc();
        Thread.sleep(100);
      }

      System.out.println("=== END LOAD PHASE " + phase + " (metaspace should have grown) ===");

      System.out.println("=== IDLE after phase " + phase + " (metaspace should shrink if elastic) ===");
      for (int i = 0; i < 10; i++) {
        System.gc();
        Thread.sleep(300);
      }
    }
  }

  private static void loadAndDropClasses() throws Exception {
    URL classPath = ElasticMetaspaceStreamDemo.class
        .getProtectionDomain()
        .getCodeSource()
        .getLocation();

    List<Object> instances = new ArrayList<>();
    try (URLClassLoader loader = new URLClassLoader(
        new URL[]{classPath},
        null // null parent: fiecare loader definește propria versiune de Target
    )) {
      for (int i = 0; i < CLASSES_PER_CYCLE; i++) {
        Class<?> clazz = Class.forName(TARGET_CLASS_NAME, true, loader);
        instances.add(clazz.getDeclaredConstructor().newInstance());
      }
    }
    // când iese din try, loader devine eligibil pentru GC => clasele și metaspace-ul lor la fel
  }

  public static class Target {
    private final byte[] payload = new byte[1024];

    public int size() {
      return payload.length;
    }
  }
}
