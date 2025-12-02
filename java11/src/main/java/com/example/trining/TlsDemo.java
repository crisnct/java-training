package com.example.trining;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.net.ssl.SSLSession;

public class TlsDemo {

  public static void main(String[] args) throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://www.google.com"))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    SSLSession session = response.sslSession().orElse(null);

    if (session != null) {
      System.out.println("Negotiated protocol: " + session.getProtocol());  // Usually TLSv1.3 in Java 11
    }
  }
}
