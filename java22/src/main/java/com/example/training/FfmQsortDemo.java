package com.example.training;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.LinkedHashMap;
import java.util.Map;

//@formatter:off
/**
 * Java 22+ FFM demo:
 * - Downcall to C strlen
 * - Upcall comparator passed to C qsort
 * - Off-heap memory via Arena
 *
 * Foreign Function & Memory (FFM) API. finalized integration for safe off-heap memory and native calls
 *
 * Build: javac FfmQsortDemo.java
 * Run:   java --enable-native-access=ALL-UNNAMED FfmQsortDemo
 */
//@formatter:on
public class FfmQsortDemo {

  // On 64-bit platforms, size_t -> long
  private static final ValueLayout.OfLong C_SIZE_T = ValueLayout.JAVA_LONG;

  private static MethodHandle STRLEN_MH;

  public static void main(String[] args) throws Throwable {
    Linker linker = Linker.nativeLinker();
    SymbolLookup stdlib = linker.defaultLookup();

    // size_t strlen(const char* s)
    STRLEN_MH = linker.downcallHandle(
        stdlib.find("strlen").orElseThrow(),
        FunctionDescriptor.of(C_SIZE_T, ValueLayout.ADDRESS)
    );

    // void qsort(void* base, size_t nmemb, size_t size, int(*compar)(const void*, const void*))
    MethodHandle QSORT_MH = linker.downcallHandle(
        stdlib.find("qsort").orElseThrow(),
        FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS,
            C_SIZE_T,
            C_SIZE_T,
            ValueLayout.ADDRESS
        )
    );

    String[] words = {"elephant", "cat", "hippopotamus", "bee", "gorilla", "ant"};

    try (Arena arena = Arena.ofConfined()) {
      // Off-heap C strings
      Map<MemorySegment, String> ptrToJava = new LinkedHashMap<>();
      MemorySegment[] cStrPtrs = new MemorySegment[words.length];
      for (int i = 0; i < words.length; i++) {
        MemorySegment cstr = arena.allocateFrom(words[i]); // null-terminated
        cStrPtrs[i] = cstr;
        ptrToJava.put(cstr, words[i]);
      }

      // Allocate char*[] and write pointers
      long ptrSize = ValueLayout.ADDRESS.byteSize();
      MemorySegment array = arena.allocate(ptrSize * words.length, ValueLayout.ADDRESS.byteAlignment());
      for (int i = 0; i < words.length; i++) {
        array.set(ValueLayout.ADDRESS, i * ptrSize, cStrPtrs[i]);
      }

      // Upcall: int cmp(const void* a, const void* b)
      MethodHandle cmpJava = MethodHandles.lookup().findStatic(
          FfmQsortDemo.class,
          "cmpByCLength",
          MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class)
      );
      MemorySegment cmpStub = linker.upcallStub(
          cmpJava,
          FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
          arena
      );

      // Sort via qsort
      QSORT_MH.invoke(array, (long) words.length, ptrSize, cmpStub);

      // Read back
      System.out.println("Sorted by C strlen (ascending):");
      for (int i = 0; i < words.length; i++) {
        MemorySegment ptr = array.get(ValueLayout.ADDRESS, i * ptrSize);
        long len = cStrLen(ptr);
        System.out.println("  " + ptrToJava.get(ptr) + " (len=" + len + ")");
      }
    }
  }

  // Comparator must not declare checked exceptions
  // Each arg is a pointer to an element (i.e., a pointer to char*).
  private static int cmpByCLength(MemorySegment a, MemorySegment b) {
    long psize = ValueLayout.ADDRESS.byteSize();

    // Reinterpret the unbounded pointer segments so we can safely read one ADDRESS
    MemorySegment aElm = a.reinterpret(psize);
    MemorySegment bElm = b.reinterpret(psize);

    MemorySegment s1 = aElm.get(ValueLayout.ADDRESS, 0); // char*
    MemorySegment s2 = bElm.get(ValueLayout.ADDRESS, 0); // char*

    long len1 = cStrLen(s1);
    long len2 = cStrLen(s2);
    return Long.compare(len1, len2);
  }

  private static long cStrLen(MemorySegment s) {
    try {
      return (long) STRLEN_MH.invoke(s);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
}
