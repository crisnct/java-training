package com.example.training.socketAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer13 {

  public static void main(String[] args) throws Exception {
    int port = args.length > 0 ? Integer.parseInt(args[0]) : 5050;
    int workers = Math.max(2, Runtime.getRuntime().availableProcessors());

    System.out.println("Using legacy impl? " + Boolean.getBoolean("jdk.net.usePlainSocketImpl"));
    System.out.println("Starting echo server on port " + port);

    ExecutorService pool = Executors.newFixedThreadPool(workers);
    try (ServerSocket server = new ServerSocket(port)) {
      while (true) {
        Socket s = server.accept();
        pool.submit(() -> handle(s));
      }
    }
  }

  private static void handle(Socket s) {
    System.out.println("Server:> New client connected on port " + s.getPort());
    try (s;
        InputStream in = new BufferedInputStream(s.getInputStream());
        OutputStream out = new BufferedOutputStream(s.getOutputStream());
        ByteArrayOutputStream received = new ByteArrayOutputStream()) {
      byte[] buf = new byte[8192];
      int n;
      while ((n = in.read(buf)) >= 0) {
        received.write(buf, 0, n);
        // echo back as we read
        out.write(buf, 0, n);
        out.flush();
      }
      System.out.println("Server:> Received message from client: " + received.toString(StandardCharsets.UTF_8));
    } catch (Exception ignored) {
      ignored.printStackTrace();
      // client closed or network error
    }
  }
}
