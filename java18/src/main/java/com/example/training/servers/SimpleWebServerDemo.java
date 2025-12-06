package com.example.training.servers;

import com.sun.net.httpserver.SimpleFileServer;
import java.net.InetSocketAddress;
import java.nio.file.Path;

//@formatter:off
/**
 * Quickly previewing static web assets.
 * Testing HTTP file access during development.
 * Serves static files from the working directory.
 * One line to start a file server without external dependencies.
 * Sharing a directory over LAN without installing Node.js or Python.
 */
//@formatter:on
public class SimpleWebServerDemo {

  public static void main(String[] args) throws Exception {
    int port = 8081;
    // Must be absolute
    Path root = Path.of(".").toAbsolutePath();
    //If you give the path to a folder where an index.html is then in the browser it will open than index.html
    //Path root = Path.of(".\\java18\\src\\main\\resources").toAbsolutePath();

    var server = SimpleFileServer.createFileServer(
        new InetSocketAddress(port),
        root,
        SimpleFileServer.OutputLevel.INFO
    );

    server.start();

    System.out.println("Serving HTTP on http://localhost:"+port);
    System.out.println("Directory: " + root);
  }
}
