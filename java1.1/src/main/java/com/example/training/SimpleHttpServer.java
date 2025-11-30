package com.example.training;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {

  public static void main(String[] args) {
    int port = 5000;

    ServerSocket server = null;

    try {
      server = new ServerSocket(port);
      System.out.println("HTTP server started on port " + port);

      while (true) {
        Socket client = server.accept();
        System.out.println("Client connected: " + client.getInetAddress());

        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();

        // Prepare HTTP response
        String body = readResourceFile();
        String response =
            "HTTP/1.0 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;

        out.write(response.getBytes());
        out.flush();

        client.close();
      }

    } catch (IOException e) {
      System.out.println("Server error: " + e.getMessage());
    } finally {
      try {
        if (server != null) {
          server.close();
        }
      } catch (IOException e) {
      }
    }
  }

  public static String readResourceFile() {
    StringBuffer buffer = new StringBuffer();
    InputStream is = null;
    DataInputStream dis = null;
    try {
      is = SimpleHttpServer.class.getResourceAsStream("../../../index.html");
      if (is == null) {
        return "ERROR: Resource file index.html not found in CLASSPATH.";
      }
      dis = new DataInputStream(is);
      String line;
      while ((line = dis.readLine()) != null) {
        buffer.append(line);
        buffer.append('\n'); // Append newline character back
      }
    } catch (IOException e) {
      System.out.println("IO Error: " + e.getMessage());
      return "ERROR: Failed to read file due to I/O exception.";
    } finally {
      try {
        if (dis != null) {
          dis.close();
        } else if (is != null) {
          is.close(); // Fallback if dis was not initialized
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return buffer.toString();
  }

}
