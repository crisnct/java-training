package com.example.training;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class NioBuffersDemo {

  public static void main(String[] args) throws Exception {
    // 1) ByteBuffer: put → flip → get, plus compact
    ByteBuffer bb = ByteBuffer.allocate(16);
    bb.put((byte) 0x41).put((byte) 0x42).put((byte) 0x43); // 'A','B','C'
    bb.flip();
    while (bb.hasRemaining()) {
      System.out.print((char) bb.get());
    }
    System.out.println();
    bb.clear();

    // 2) Primitive views pe același ByteBuffer
    //    (ordine implicită: BIG_ENDIAN; o schimbăm și observăm efectul)
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.putInt(0x11223344).flip();
    System.out.println("BIG_ENDIAN first byte: 0x" + toHex(bb.get())); // 0x11
    bb.clear();

    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putInt(0x11223344).flip();
    System.out.println("LITTLE_ENDIAN first byte: 0x" + toHex(bb.get())); // 0x44
    bb.clear();

    // 3) Buffere specializate (Short/Int/Long/Float/Double) via "view buffers"
    bb.order(ByteOrder.BIG_ENDIAN);
    ShortBuffer sb = bb.asShortBuffer();
    sb.put((short) 1234).put((short) -2);
    IntBuffer ib = bb.asIntBuffer();
    ib.put(0, 0x7f00ff00); // scrie peste primele 4 bytes din bb
    LongBuffer lb = bb.asLongBuffer();
    lb.put(0, 123456789L);
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(0, 3.14f);
    DoubleBuffer db = bb.asDoubleBuffer();
    db.put(0, 2.718281828);

    // 4) CharBuffer: manipulare de text în memorie
    CharBuffer cb = CharBuffer.allocate(16);
    cb.put('J').put('a').put('v').put('a');
    cb.flip();
    System.out.print("CharBuffer: ");
    while (cb.hasRemaining()) {
      System.out.print(cb.get());
    }
    System.out.println();
    cb.clear();

    // 5) Charset encode/decode (UTF-8) cu encoders/decoders NIO
    Charset cs = Charset.forName("UTF-8");
    CharsetEncoder enc = cs.newEncoder();
    CharsetDecoder dec = cs.newDecoder();
    CharBuffer text = CharBuffer.wrap("Învăț NIO 1.4");
    ByteBuffer bytes = enc.encode(text);     // char -> bytes
    bytes.flip(); // defensiv, deși encode întoarce de obicei buffer gata de citire
    CharBuffer back = dec.decode(bytes);     // bytes -> char
    System.out.println("Decoded text: " + back.toString());

    // 6) FileChannel + Memory-mapped file (MappedByteBuffer)
    File tmp = new File("nio14-buffers-demo.bin");
    RandomAccessFile raf = null;
    FileChannel ch = null;
    try {
      raf = new RandomAccessFile(tmp, "rw");
      ch = raf.getChannel();
      int size = 64;
      MappedByteBuffer map = ch.map(FileChannel.MapMode.READ_WRITE, 0, size);

      // scriem text UTF-8 în mapped buffer (folosim encoder direct într-un ByteBuffer temporar)
      CharBuffer hello = CharBuffer.wrap("Hello MMap!\n");
      ByteBuffer encBuf = enc.encode(hello);
      encBuf.flip();
      while (encBuf.hasRemaining()) {
        map.put(encBuf.get());
      }

      // poziționăm canalul și citim înapoi într-un ByteBuffer NIO normal
      ch.position(0);
      ByteBuffer read = ByteBuffer.allocate(size);
      ch.read(read);
      read.flip();
      CharBuffer decoded = dec.decode(read);
      System.out.println("Mapped read back:\n" + decoded.toString().trim());
    } finally {
      if (ch != null) {
        try {
          ch.close();
        } catch (Exception ignore) {
        }
      }
      if (raf != null) {
        try {
          raf.close();
        } catch (Exception ignore) {
        }
      }
      try {
        tmp.delete();
      } catch (Exception ignore) {
      }
    }

    // 7) Mic util: afișează meta-informații de Buffer
    printBufferState("ByteBuffer", bb);
  }

  private static void printBufferState(String name, Buffer b) {
    System.out.println(name + " pos=" + b.position() + " lim=" + b.limit() + " cap=" + b.capacity());
  }

  private static String toHex(byte b) {
    int v = b & 0xFF;
    String s = Integer.toHexString(v).toUpperCase();
    return (s.length() == 1 ? "0" + s : s);
  }
}
