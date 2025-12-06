package com.example.training.hidden;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class HiddenClassExample {

  public static void main(String[] args) throws Throwable {
    byte[] classBytes = loadClassBytes(Template.class);

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    // Create a hidden class based on Template.class bytes
    Class<?> hiddenClass = lookup
        .defineHiddenClass(classBytes, true, MethodHandles.Lookup.ClassOption.NESTMATE)
        .lookupClass();

    System.out.println("Hidden: " + hiddenClass.isHidden());
    System.out.println("Name:   " + hiddenClass.getName());

    // Use MH to call the static method "message" on the hidden class
    MethodHandles.Lookup hiddenLookup =
        MethodHandles.privateLookupIn(hiddenClass, lookup);

    MethodHandle handle = hiddenLookup.findStatic(
        hiddenClass,
        "message",
        MethodType.methodType(String.class)
    );

    String result = (String) handle.invokeExact();
    System.out.println("Result: " + result);
  }

  private static byte[] loadClassBytes(Class<?> clazz) throws IOException {
    String resourceName = clazz.getSimpleName() + ".class";
    try (InputStream in = clazz.getResourceAsStream(resourceName)) {
      if (in == null) {
        throw new IllegalStateException("Cannot find resource: " + resourceName);
      }
      return in.readAllBytes();
    }
  }
}
