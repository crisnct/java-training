package com.example.trining;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP/2 support + Asynchronous API Java 11 HTTP Client supports HTTP/1.1 and HTTP/2,
 * automatically negotiating the protocol. Asynchronous
 * programming uses CompletableFuture.
 */
public class HttpAsyncDemo {

  public static void main(String[] args) {
    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)   // request HTTP/2
        .build();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://httpbin.org/get"))
        .build();

    CompletableFuture<Void> future =
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
              System.out.println("Status: " + response.statusCode());
              System.out.println("Body: " + response.body());
            });

    future.join();  // Wait for async completion
  }
}
