package com.example.training.servers;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.net.InetSocketAddress;
import java.nio.file.Path;
//@formatter:off
/**
 * What this demonstrates
 * Uses SimpleFileServer.createFileHandler directly.
 * A request handler that serves static files. It does not start or configure a server.
 * Serves all files from the specified directory under the /static path.
 * Gives explicit routing control instead of a root-level file server.
 */
//@formatter:on
public class FileHandlerDemo {

  public static void main(String[] args) throws Exception {
    int port = 8081;

    // Must be an absolute directory path
    Path root = Path.of(".").toAbsolutePath();

    // Create the low-level HttpServer
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    // Create a file handler that serves static content under /static
    var handler = SimpleFileServer.createFileHandler(root);

    // Mount handler to a specific context
    server.createContext("/static", handler);

    server.start();

    System.out.println("HTTP static file server running...");
    System.out.println("Open http://localhost:" + port + "/static/");
    System.out.println("Serving directory: " + root);
  }
}
