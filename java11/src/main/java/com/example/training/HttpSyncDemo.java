package com.example.training;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HTTP Client API (standardized in Java 11) The API moved out of incubation and became part of the JDK under java.net.http.
 */
public class HttpSyncDemo {

  public static void main(String[] args) throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://httpbin.org/get"))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println(response.statusCode());
    System.out.println(response.body());
  }
}
