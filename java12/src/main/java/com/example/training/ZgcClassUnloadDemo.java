package com.example.training;

/**
 * You can’t see “concurrent” directly in code, but you can:
 * Turn on ZGC.
 * Load lots of classes via custom classloaders.
 * Enable GC+class logging and see that class unloading happens without long stop-the-world pauses.
 */
public class ZgcClassUnloadDemo {

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 10_000; i++) {
      // Create a throwaway classloader every iteration
      ClassLoader loader = new java.net.URLClassLoader(
          ((java.net.URLClassLoader) ZgcClassUnloadDemo.class.getClassLoader()).getURLs(),
          null
      );

      // Load some class through it
      Class<?> c = Class.forName("java.lang.String", false, loader);

      // Drop reference to class + loader, encourage GC
      if (i % 1000 == 0) {
        System.out.println("Iteration " + i);
        System.gc(); // just as a hint
        Thread.sleep(10);
      }
    }

    System.out.println("Done. Inspect GC logs for class unloading.");
  }
}
