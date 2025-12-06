package com.example.training.servers;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.net.InetSocketAddress;
import java.nio.file.Path;
//@formatter:off
/**
 * When this is useful:
 * For simple static file serving with request/response logging — useful in dev, prototyping or test setups.
 * When you don’t want a full-blown web server stack, but still want visibility into what is being requested and served.
 */
//@formatter:on
public class StaticServerWithLogging {

  public static void main(String[] args) throws Exception {
    Path root = Path.of(".").toAbsolutePath();  // must be absolute
    int port = 8080;

    // Create a logging/filter that prints to stdout
    Filter logFilter = SimpleFileServer.createOutputFilter(
        System.out,
        SimpleFileServer.OutputLevel.INFO
    );

    // Create the server: backlog 10, serve root context, static handler + our log filter
    HttpServer server = HttpServer.create(
        new InetSocketAddress(port),
        10,
        "/",                                // root context path
        SimpleFileServer.createFileHandler(root),  // static file handler
        logFilter
    );

    server.start();

    System.out.println("Server started on http://localhost:" + port);
    System.out.println("Serving directory: " + root);
  }
}
