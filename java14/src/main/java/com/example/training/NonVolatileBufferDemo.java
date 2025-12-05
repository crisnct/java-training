package com.example.training;// File: NonVolatileBufferDemo.java

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// @formatter:off
/**
 * JEP 352 (Non-Volatile Mapped Byte Buffers), introduced in Java 14, adds support for mapping files residing on persistent memory (NVDIMM/PMEM)
 * using new non-volatile mapping modes. These buffers let data survive JVM or system restarts because they are backed by
 * persistent memory rather than volatile DRAM.
 * In normal environments (without PMEM hardware), the API still works but behaves like a standard memory-mapped file.
 *
 * Serialization = “Let Java handle the format for me — I just want the same object back later.”
 * Mapped ByteBuffer / Binary file = “I want raw, structured, high-performance control over bytes — possibly to share between processes or languages.”
 */
// @formatter:on
public class NonVolatileBufferDemo {

  public static void main(String[] args) throws IOException {
    Path file = Path.of("persistent_data.bin");

    // Create or open the file in read/write mode
    try (FileChannel channel = FileChannel.open(
        file,
        StandardOpenOption.CREATE,
        StandardOpenOption.READ,
        StandardOpenOption.WRITE)) {

      // Map 1 MB of file into non-volatile memory (requires PMEM support)
      // On regular systems, this behaves like a normal memory-mapped file.
      MappedByteBuffer buffer = channel.map(
          FileChannel.MapMode.READ_WRITE,  // MapMode can also be PRIVATE or READ_ONLY
          0,
          1024 * 1024
      );

      // Write some data
      String message = "Data that may persist across reboots.";
      buffer.put(message.getBytes());

      // Force changes to be written to the underlying storage device
      buffer.force();

      System.out.println("Message written to non-volatile buffer.");
    }
  }
}
