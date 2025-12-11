package com.example.training;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Java 23 – JEP 471
 * <p>
 * Demonstrates the intended migration paths away from deprecated sun.misc.Unsafe memory-access methods:
 * <p>
 * 1) VarHandle for on-heap atomic access 2) Foreign Function & Memory (FFM) API for off-heap/native memory
 */
public class UnsafeMigrationDemo {

  // === 1. VarHandle instead of Unsafe on-heap field access ===

  /**
   * OLD STYLE (now deprecated in Java 23):
   * <p>
   * Unsafe unsafe = ...; long offset = unsafe.objectFieldOffset(Account.class.getDeclaredField("balance")); long old = unsafe.getAndAddLong(account,
   * offset, 50L);
   * <p>
   * NEW STYLE: VarHandle
   */
  private static final VarHandle BALANCE_HANDLE;

  static {
    try {
      BALANCE_HANDLE = MethodHandles.lookup()
          .findVarHandle(Account.class, "balance", long.class);
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private static final class Account {

    // keep it volatile for visibility; VarHandle gives us atomic operations
    volatile long balance;

    Account(long initialBalance) {
      this.balance = initialBalance;
    }
  }

  // === 2. FFM instead of Unsafe off-heap/native memory access ===

  /**
   * OLD STYLE (now deprecated in Java 23):
   * <p>
   * long address = unsafe.allocateMemory(16); unsafe.putInt(address, 42); int v = unsafe.getInt(address); unsafe.freeMemory(address);
   * <p>
   * NEW STYLE: Foreign Function & Memory API
   */
  private static void demoNativeMemory() {
    // Confined arena: memory is automatically freed when arena is closed
    try (Arena arena = Arena.ofConfined()) {
      // Allocate 16 bytes of native memory
      MemorySegment segment = arena.allocate(16);

      // Write values using typed layouts
      segment.set(ValueLayout.JAVA_INT, 0, 42);           // first 4 bytes
      segment.set(ValueLayout.JAVA_LONG, 8, 123_456L);    // bytes [8..15]

      // Read them back
      int intValue = segment.get(ValueLayout.JAVA_INT, 0);
      long longValue = segment.get(ValueLayout.JAVA_LONG, 8);

      System.out.println("[FFM] int  from native memory = " + intValue);
      System.out.println("[FFM] long from native memory = " + longValue);
    } // memory is released here
  }

  public static void main(String[] args) {
    System.out.println("=== VarHandle example (on-heap) ===");
    Account account = new Account(100);

    long oldBalance = (long) BALANCE_HANDLE.getAndAdd(account, 50L);
    long newBalance = account.balance;

    System.out.println("Old balance: " + oldBalance);
    System.out.println("New balance: " + newBalance);

    System.out.println();
    System.out.println("=== Foreign Function & Memory example (off-heap) ===");
    demoNativeMemory();
  }
}
