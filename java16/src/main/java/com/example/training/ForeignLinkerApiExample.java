package com.example.training;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.LibraryLookup;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

/**
 * VM options:
 * --add-modules jdk.incubator.foreign -Dforeign.restricted=permit
 */
public class ForeignLinkerApiExample {

  public static void main(String[] args) throws Throwable {
    System.out.println("java.version = " + System.getProperty("java.version"));
    System.out.println("os.name      = " + System.getProperty("os.name"));
    System.out.println("os.arch      = " + System.getProperty("os.arch"));

    CLinker linker = CLinker.getInstance();
    LibraryLookup stdlib = LibraryLookup.ofDefault();

    // C function: size_t strlen(const char *s);
    // On Windows x64, size_t is 64-bit (long long) => use C_LONG_LONG + Java long
    MethodHandle strlenHandle = linker.downcallHandle(
        stdlib.lookup("strlen").get(),
        MethodType.methodType(long.class, MemoryAddress.class),
        FunctionDescriptor.of(CLinker.C_LONG_LONG, CLinker.C_POINTER)
    );

    String text = "Panama€ășț";

    try (MemorySegment cString = CLinker.toCString(text)) {
      long nativeLength = (long) strlenHandle.invokeExact(cString.address());
      System.out.println("Java   length(): " + text.length());
      System.out.println("Native strlen(): " + nativeLength);
    }
  }
}
