package com.example.training;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple, single-threaded HTTP server compatible with Java 1.2.
 * The key changes involve using the Java 1.1/1.2 Reader classes for reliable text handling.
 */
public class SimpleHttpServer {

  public static void main(String[] args) {
    int port = 5000;
    ServerSocket server = null;

    try {
      // 1. Create the ServerSocket
      server = new ServerSocket(port);
      System.out.println("HTTP server started on port " + port);

      while (true) {
        // 2. Accept incoming client connection (blocking operation)
        Socket client = server.accept();
        System.out.println("Client connected: " + client.getInetAddress());

        // We don't read the request here, but in a real server, we would use 
        // a BufferedReader on the InputStream to read the request headers line by line.

        OutputStream out = client.getOutputStream();

        // Prepare HTTP response
        String body = readResourceFile();
        
        // Use a consistent character set (e.g., ISO-8859-1 or UTF-8) for the body 
        // to ensure the Content-Length is accurate when converting to bytes.
        byte[] bodyBytes = body.getBytes("UTF-8");
        
        String responseHeaders = 
            "HTTP/1.0 200 OK\r\n" +
            "Content-Type: text/html; charset=UTF-8\r\n" + // Specify charset
            "Content-Length: " + bodyBytes.length + "\r\n" +
            "\r\n";

        // Write headers and then the body bytes
        out.write(responseHeaders.getBytes("UTF-8"));
        out.write(bodyBytes);
        out.flush();

        // Close the connection
        client.close();
        System.out.println("Client connection closed.");
      }

    } catch (IOException e) {
      System.out.println("Server error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        if (server != null) {
          server.close();
        }
      } catch (IOException e) {
        // Ignored
      }
    }
  }

  /**
   * Reads a resource file from the classpath using Java 1.2's preferred text I/O.
   * This method uses InputStreamReader and BufferedReader for correct character
   * encoding, replacing the deprecated DataInputStream.readLine().
   * * @return The content of index.html as a String.
   */
  public static String readResourceFile() {
    StringBuffer buffer = new StringBuffer();
    InputStream is = null;
    BufferedReader reader = null;
    
    // NOTE: Using the relative path from the user's original code
    String resourcePath = "../../../index.html"; 
    
    try {
      // Get the InputStream for the resource
      is = SimpleHttpServer.class.getResourceAsStream(resourcePath);
      
      if (is == null) {
        return "ERROR: Resource file index.html not found in CLASSPATH.";
      }
      
      // JAVA 1.2 IMPROVEMENT: Use InputStreamReader to handle byte-to-char conversion 
      // correctly, specifying the character set (UTF-8 is recommended for web content).
      InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      
      // JAVA 1.2 IMPROVEMENT: Use BufferedReader for efficient, reliable line-by-line reading.
      reader = new BufferedReader(isr);
      
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
        buffer.append('\n'); // Append newline character back
      }
      
    } catch (IOException e) {
      System.out.println("IO Error when reading resource: " + e.getMessage());
      return "ERROR: Failed to read file due to I/O exception.";
    } finally {
      // Clean up resources: close the BufferedReader (which also closes the underlying stream)
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        // Ignore secondary exception during closing
      }
    }

    return buffer.toString();
  }
}