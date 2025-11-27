package com.example.training.asyncSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncSocketServerDemo {

  public static void main(String[] args) throws Exception {
    new AsyncSocketServerDemo().startServer();
  }

  public void startServer() throws IOException, ExecutionException, InterruptedException {
    try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000))) {
      System.out.println("Async server listening on port 5000...");

      // Wait for client connection
      Future<AsynchronousSocketChannel> acceptFuture = server.accept();
      try (AsynchronousSocketChannel clientChannel = acceptFuture.get()) {
        System.out.println("Client connected.");

        // Allocate buffer
        ByteBuffer buffer = ByteBuffer.allocate(256);

        // Read data asynchronously
        Future<Integer> readFuture = clientChannel.read(buffer);
        int bytesRead = readFuture.get();

        if (bytesRead > 0) {
          buffer.flip();
          byte[] data = new byte[bytesRead];
          buffer.get(data);
          System.out.println("Received: " + new String(data, StandardCharsets.UTF_8));
        } else {
          System.out.println("No data received.");
        }
        System.out.println("Server closed.");
      }
    }
  }
}
