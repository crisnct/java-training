package com.example.training;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Demonstrație NIO (Java 1.4): - Non-blocking server cu Selector (eco) - ByteBuffer/CharBuffer - FileChannel: memory-mapped + file locking - Charset
 * encode/decode
 */
public class ServerClientNioDemo {

  public static void main(String[] args) throws Exception {
    // 1) Pornește un server non-blocking pe un fir separat
    final int port = 9093;
    Thread server = new Thread(new Runnable() {
      public void run() {
        try {
          runEchoServer(port);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }, "nio-echo-server");
    server.setDaemon(true);
    server.start();

    // 2) Dă serverului o clipă să pornească
    Thread.sleep(200);

    // 3) Rulează un client simplu (SocketChannel, blocking) ca să vedem eco
    runSimpleClient("localhost", port, "Salut din NIO 1.4!\n");

    // 4) Buffers + Charset (encode/decode)
    buffersAndCharsetDemo();

    // 5) FileChannel: memory map + lock
    fileChannelMapAndLockDemo();

    System.out.println("[main] gata");
  }

  // ------------------------ 1) NON-BLOCKING ECHO SERVER ------------------------

  private static void runEchoServer(int port) throws IOException {
    Selector selector = null;
    ServerSocketChannel server = null;
    try {
      selector = Selector.open();

      server = ServerSocketChannel.open();
      server.configureBlocking(false);
      server.socket().bind(new InetSocketAddress(port));

      // ne înregistrăm pentru ACCEPT
      server.register(selector, SelectionKey.OP_ACCEPT);
      System.out.println("[srv] listening on " + port);

      ByteBuffer buf = ByteBuffer.allocate(4096);

      while (true) {
        // așteaptă evenimente (non-blocking I/O scalabil)
        selector.select(1000); // timeout mic, doar pentru demo
        Set selected = selector.selectedKeys();
        if (selected.isEmpty()) {
          continue;
        }
        for (Iterator it = selected.iterator(); it.hasNext(); ) {
          SelectionKey key = (SelectionKey) it.next();
          it.remove();

          if (!key.isValid()) {
            continue;
          }

          if (key.isAcceptable()) {
            // accept noul client, pune canalul non-blocking și înregistrează pentru READ
            SocketChannel sc = server.accept();
            if (sc == null) {
              continue;
            }
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println("[srv] accept " + sc.socket().getRemoteSocketAddress());
          } else if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            buf.clear();
            int read = -1;
            try {
              read = sc.read(buf);
            } catch (IOException ioe) {
              read = -1;
            }
            if (read <= 0) {
              // client închis sau eroare → închidem
              safeClose(sc);
              key.cancel();
              continue;
            }
            // pregătim pentru citire din buffer
            buf.flip();

            // eco: scriem înapoi ce am primit (poate în mai multe write-uri)
            while (buf.hasRemaining()) {
              sc.write(buf);
            }
            // Gata cu acest eveniment de citire
          }
        }
      }
    } finally {
      safeClose(server);
      safeClose(selector);
    }
  }

  private static void runSimpleClient(String host, int port, String msg) {
    SocketChannel sc = null;
    try {
      sc = SocketChannel.open();
      sc.configureBlocking(true); // clientul nostru poate fi blocking, simplu
      sc.connect(new InetSocketAddress(host, port));

      ByteBuffer out = ByteBuffer.allocate(1024);
      out.put(msg.getBytes("UTF-8"));
      out.flip();
      while (out.hasRemaining()) {
        sc.write(out);
      }

      // citește răspunsul (eco) — blocking read cu timeout la nivel de socket nu e în NIO 1.4,
      // păstrăm simplu pentru demo
      ByteBuffer in = ByteBuffer.allocate(1024);
      int read = sc.read(in);
      if (read > 0) {
        in.flip();
        byte[] bytes = new byte[in.remaining()];
        in.get(bytes);
        System.out.println("[cli] eco: " + new String(bytes, "UTF-8").trim());
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      safeClose(sc);
    }
  }

  // ------------------------ 2) BUFFERS + CHARSET ------------------------

  private static void buffersAndCharsetDemo() throws Exception {
    System.out.println("[buf] demo ByteBuffer/CharBuffer + Charset");

    // ByteBuffer: put → flip → get
    ByteBuffer bb = ByteBuffer.allocate(16);
    bb.put((byte) 0x41);               // 'A'
    bb.put((byte) 0x42);               // 'B'
    bb.put((byte) 0x43);               // 'C'
    bb.flip();                        // pregătim pentru citire
    while (bb.hasRemaining()) {
      byte b = bb.get();
      System.out.print((char) b);
    }
    System.out.println();

    // Charset: UTF-8 encoder/decoder cu buffers NIO
    Charset cs = Charset.forName("UTF-8");
    CharsetEncoder enc = cs.newEncoder();
    CharsetDecoder dec = cs.newDecoder();

    CharBuffer cb = CharBuffer.wrap("Învăț NIO 1.4");
    ByteBuffer encoded = enc.encode(cb);     // char → bytes
    CharBuffer decoded = dec.decode(encoded); // bytes → char

    System.out.println("[buf] decoded: " + decoded.toString());

    // Demonstrăm compact(): scriem și păstrăm restul
    ByteBuffer x = ByteBuffer.allocate(8);
    x.put((byte) 1).put((byte) 2).put((byte) 3);
    x.flip();
    x.get(); // consumăm un byte
    x.compact(); // mută byte-urile rămase la început (2,3)
    x.put((byte) 4).put((byte) 5);
    x.flip();
    System.out.print("[buf] after compact: ");
    while (x.hasRemaining()) {
      System.out.print(x.get() + " ");
    }
    System.out.println();
  }

  // ------------------------ 3) FILECHANNEL: MMAP + LOCK ------------------------

  private static void fileChannelMapAndLockDemo() throws Exception {
    System.out.println("[file] demo FileChannel: map + lock");

    File f = new File("nio14-demo.bin");
    RandomAccessFile raf = null;
    FileChannel ch = null;
    FileLock lock = null;
    try {
      raf = new RandomAccessFile(f, "rw");
      ch = raf.getChannel();

      // LOCK exclusiv pe întreg fișierul (previne acces concurent în alt proces)
      lock = ch.lock(); // blocant; pentru non-blocking: tryLock()

      // Memory-map: scriem rapid bytes
      int size = 64;
      MappedByteBuffer mb = ch.map(FileChannel.MapMode.READ_WRITE, 0, size);
      mb.put("Hello via MappedByteBuffer!\n".getBytes("UTF-8"));
      mb.putInt(42);
      mb.force(); // asigură flush către disc

      // Citire înapoi prin poziționare în canal
      ch.position(0);
      ByteBuffer readBuf = ByteBuffer.allocate(size);
      ch.read(readBuf);
      readBuf.flip();

      byte[] all = new byte[readBuf.remaining()];
      readBuf.get(all);
      System.out.println("[file] content:\n" + new String(all, "UTF-8").trim());
    } finally {
      safeRelease(lock);
      safeClose(ch);
      safeClose(raf);
      // curățare fișier demo (opțional)
      try {
        f.delete();
      } catch (Exception ignore) {
      }
    }
  }

  // ------------------------ Helpers ------------------------

  private static void safeClose(Selector s) {
    if (s != null) {
      try {
        s.close();
      } catch (Exception ignore) {
      }
    }
  }

  private static void safeClose(Channel c) {
    if (c != null) {
      try {
        c.close();
      } catch (Exception ignore) {
      }
    }
  }

  private static void safeClose(RandomAccessFile r) {
    if (r != null) {
      try {
        r.close();
      } catch (Exception ignore) {
      }
    }
  }

  private static void safeRelease(FileLock l) {
    if (l != null) {
      try {
        l.release();
      } catch (Exception ignore) {
      }
    }
  }
}
