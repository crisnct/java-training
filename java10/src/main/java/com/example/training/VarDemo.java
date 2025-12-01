package com.example.training;

import java.io.IOException;

public class VarDemo {

  public static void main(String[] args) throws IOException {
    // 1. Local variable type inference: compiler infers String
    var message = "Hello from Java 10";

    // 2. Boilerplate reduction: instead of
    // StringBuilder builder = new StringBuilder();
    var builder = new StringBuilder();
    builder.append(message).append("!");

    // 3. Enhanced for-loop with var
    var names = java.util.List.of("Ana", "Bogdan", "Cristian");
    for (var name : names) {
      System.out.println("Name: " + name);
    }

    // 4. try-with-resources using var
   // Write some lines into a ByteArrayOutputStream
    var out = new java.io.ByteArrayOutputStream();
    out.write("Line 1\n".getBytes());
    out.write("Line 2\n".getBytes());
    out.write("Line 3\n".getBytes());

    // Convert to ByteArrayInputStream and read the lines
    try (var in = new java.io.ByteArrayInputStream(out.toByteArray());
        var reader = new java.io.BufferedReader(new java.io.InputStreamReader(in))) {

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 5. Demonstrates that runtime type is unchanged
    var number = 10;              // inferred int
    System.out.println(((Object) number).getClass());  // prints: class java.lang.Integer

    // Not allowed:
    // var x;                           // ❌ no initializer
    // var fieldInsideClass;            // ❌ cannot be a field
    // void method(var param) {}        // ❌ invalid for parameters
    // var method() { return 1; }       // ❌ invalid for return type
  }
}
