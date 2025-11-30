package com.example.training;// File: Http2Demo.java
// NOTE: Java 9 needs:   --add-modules jdk.incubator.httpclient

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import jdk.incubator.http.WebSocket;
import jdk.incubator.http.WebSocket.MessagePart;

/**
 * See this in pom.xml
 *  <compilerArgs>
 *     <arg>--add-modules</arg>
 *      <arg>jdk.incubator.httpclient</arg>
 *  </compilerArgs>
 */
public class Http2Demo {

  public static void main(String[] args) throws Exception {
    // 1) Create modern HTTP client with HTTP/2 enabled
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    // 2) Send async GET using CompletableFuture
    HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI("https://http2.github.io/"))
        .GET()
        .build();

    CompletableFuture<HttpResponse<String>> future =
        client.sendAsync(request, HttpResponse.BodyHandler.asString());

    System.out.println("Request sent asynchronously...");

    // Handle async response
    future.thenAccept(response -> {
      System.out.println("HTTP Version: " + response.version());
      System.out.println("Status Code: " + response.statusCode());
      System.out.println("Body length: " + response.body().length());
    }).join();

    // 3) WebSocket support (simple connect + message)
    CompletableFuture<WebSocket> wsFuture = client
        .newWebSocketBuilder(new URI("wss://ws.postman-echo.com/raw"), new WebSocket.Listener() {
          @Override
          public CompletionStage<?> onText(WebSocket webSocket, CharSequence message, MessagePart part) {
            System.out.println("WebSocket received: " + message);
            return CompletableFuture.completedFuture(null);
          }

          @Override
          public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("WebSocket error: " + error.getMessage());
          }
        }).buildAsync()
        .exceptionally(error -> {
          System.err.println("Failed to connect WebSocket: " + error.getMessage());
          return null;
        });

    WebSocket ws = wsFuture.join();
    ws.sendText("Hello from Java 9 WebSocket!", true);

    // Small delay so the message can return
    Thread.sleep(2000);
    ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
  }
}
