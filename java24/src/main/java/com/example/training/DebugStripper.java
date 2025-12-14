package com.example.training;// DebugStripper.java (Java 24)

import java.io.IOException;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.MethodModel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;

/**
 * Strip methods named "debug*" from a compiled class file.
 * <p>
 * Usage: javac DebugStripper.java java DebugStripper path\to\MyClass.class  path\to\MyClass_stripped.class
 * <p>
 * Notes: - Uses java.lang.classfile API (final in JDK 24) — no preview flags needed. - Leaves everything else intact (fields, other methods,
 * attributes, stack maps).
 */
public class DebugStripper {

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Usage: java DebugStripper <in.class> <out.class>");
      System.exit(1);
    }
    Path in = Path.of(args[0]);
    Path out = Path.of(args[1]);

    byte[] bytes = Files.readAllBytes(in);

    // Parse the class
    ClassFile cf = ClassFile.of();
    ClassModel model = cf.parse(bytes);

    System.out.println("Input:  " + model.thisClass().asInternalName()
        + "  (sha1=" + sha1(bytes) + ")");
    System.out.println("Methods before:");
    model.methods().forEach(m ->
        System.out.println("  " + m.methodName().stringValue() + " " + m.methodTypeSymbol()));

    // Transform: copy all elements except methods whose names start with "debug"
    byte[] newBytes = cf.build(model.thisClass().asSymbol(), classBuilder -> {
      for (ClassElement ce : model) {
        if (ce instanceof MethodModel mm) {
          String name = mm.methodName().stringValue();
          if (name.startsWith("debug")) {
            // drop it
            continue;
          }
        }
        classBuilder.with(ce); // keep element as-is
      }
    });

    Files.createDirectories(out.getParent() != null ? out.getParent() : Path.of("."));
    Files.write(out, newBytes);

    // Show result summary
    ClassModel after = cf.parse(newBytes);
    System.out.println("\nOutput: " + after.thisClass().asInternalName()
        + "  (sha1=" + sha1(newBytes) + ")");
    System.out.println("Methods after:");
    after.methods().forEach(m ->
        System.out.println("  " + m.methodName().stringValue() + " " + m.methodTypeSymbol()));
    System.out.println("\nDone. Removed any methods named debug*.");
  }

  private static String sha1(byte[] data) {
    try {
      var md = java.security.MessageDigest.getInstance("SHA-1");
      return HexFormat.of().formatHex(md.digest(data));
    } catch (Exception e) {
      return "n/a";
    }
  }
}
