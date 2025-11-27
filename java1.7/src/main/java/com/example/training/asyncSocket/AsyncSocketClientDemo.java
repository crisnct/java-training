package com.example.training.asyncSocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class AsyncSocketClientDemo {

  public static void main(String[] args) throws Exception {
    new AsyncSocketClientDemo().sendMessage();
  }

  public void sendMessage() throws Exception {
    try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open()) {
      InetSocketAddress serverAddress = new InetSocketAddress("localhost", 5000);
      // Connect asynchronously (we block on Future just to keep the demo simple)
      Future<Void> connectFuture = channel.connect(serverAddress);
      connectFuture.get(); // wait until connected

      ByteBuffer buffer = ByteBuffer.wrap("Hello from Java 7".getBytes(StandardCharsets.UTF_8));

      // Write asynchronously (again, block on Future in this simple demo)
      Future<Integer> writeFuture = channel.write(buffer);
      Integer bytesWritten = writeFuture.get();

      System.out.println("Bytes written: " + bytesWritten);
    }
  }
}
