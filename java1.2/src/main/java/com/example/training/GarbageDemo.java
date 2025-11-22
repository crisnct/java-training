package com.example.training;

public class GarbageDemo {

  public static void main(String[] args) throws InterruptedException {
    //Set this fo VM options: -Xmx256m -Xms256m -verbose:gc

    System.out.println("== START GC DEMO ==");

    // 1. Obiecte scurte, create și distruse rapid
    for (int i = 0; i < 1000; i++) {
      createShortLivedObjects();
    }

    // 2. If result is not stored in a variable then GC will clean memory allocated for the big object
    Object bigobj = createLongLivedObject();

    // Forțăm câteva GC-uri pentru a observa comportamentul
    for (int i = 0; i < 5; i++) {
      System.gc();
      Thread.sleep(500);
      System.out.println("GC cycle " + (i + 1) + " done");
    }

    System.out.println("== END GC DEMO ==");
  }

  private static void createShortLivedObjects() {
    // Creează multe obiecte mici care mor rapid
    for (int i = 0; i < 10000; i++) {
      String tmp = new String("temp" + i);
    }
  }

  private static Object createLongLivedObject() {
    // Creează un obiect care rămâne referențiat
    byte[] data = new byte[10 * 1024 * 1024]; // 10 MB
    System.out.println("Created a long-lived 10MB object");
    return data;
  }
}
