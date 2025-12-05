package com.example.training.socketAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class EchoClientBench13 {

  public static void main(String[] args) throws Exception {
    String host = args.length > 0 ? args[0] : "127.0.0.1";
    int port = args.length > 1 ? Integer.parseInt(args[1]) : 5050;
    int clients = 10;

    System.out.printf("Legacy impl? %s, host=%s, port=%d, clients=%d",
        Boolean.getBoolean("jdk.net.usePlainSocketImpl"), host, port, clients);

    ExecutorService pool = Executors.newFixedThreadPool(Math.min(clients, 256));
    List<Callable<Long>> tasks = new ArrayList<>();

    for (int c = 0; c < clients; c++) {
      final int clientId = c;
      tasks.add(() -> runClient(host, port, clientId));
    }

    long t0 = System.nanoTime();
    List<Future<Long>> results = pool.invokeAll(tasks);
    pool.shutdown();
    pool.awaitTermination(1, TimeUnit.MINUTES);
    long t1 = System.nanoTime();

    long totalBytes = 0;
    for (Future<Long> f : results) {
      totalBytes += f.get();
    }

    double seconds = (t1 - t0) / 1_000_000_000.0;
    double mb = totalBytes / (1024.0 * 1024.0);
    System.out.printf("Elapsed: %.3fs, Throughput: %.2f MB/s%n", seconds, mb / seconds);
  }

  private static long runClient(String host, int port, int clientId) {
    String content = "Hello from client " + clientId;
    long bytes = 0;
    byte[] data = content.getBytes(StandardCharsets.UTF_8);

    try (Socket s = new Socket(host, port);
        InputStream in = new BufferedInputStream(s.getInputStream());
        OutputStream out = new BufferedOutputStream(s.getOutputStream())) {

      byte[] buf = new byte[data.length];

      println(clientId, "Sending message:"+ content);
      out.write(data);
      out.flush();
      // signal end of message so server can respond
      s.shutdownOutput();
      println(clientId, "Waiting for echo from server...");
      int read = 0;
      while (read < data.length) {
        int n = in.read(buf, read, data.length - read);
        if (n < 0) {
          throw new EOFException("Client:> server closed");
        }
        read += n;
      }
      bytes += read;
      String echoResponse = new String(buf, 0, read, StandardCharsets.UTF_8);
      if (Objects.equals(echoResponse, content)) {
        println(clientId, "Received echo from server. Message was received successfully by the server.");
      } else {
        println(clientId, "Received echo from server. Message was altered during communication with server.");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return bytes;
  }

  private static void println(int clientId, String message) {
    System.out.println("Client " + clientId + ":>" + message);
  }
}
