package com.example.training.spiRegistration;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//@formatter:off
/**
 * Main entry point to demonstrate JEP 418 in action.
 *
 * If the SPI is wired correctly, InetAddress.getByName()
 * will go through DemoInetAddressResolver instead of OS DNS.
 *
 * DNS:
 * Your Java app calls InetAddress.getByName("google.com")
 * Java delegates to the OS DNS resolver (Windows, Linux, macOS)
 * The OS talks to real DNS servers somewhere on the network
 * You get a real IP address back
 *
 * JEP 418 changes one thing:
 * It gives you a hook to replace the OS DNS resolver with DemoInetAddressResolver
 *
 * Real life usage example:
 * 1. DNS-over-HTTPS (DoH) client inside a JVM service
 * Problem before Java 18
 * You wanted encrypted DNS queries (DoH) for privacy or corporate policy.
 * You had to write a native resolver, run a local DoH proxy, or hack /etc/hosts.
 * After Java 18
 * Implement a resolver using InetAddressResolver
 * Send DNS queries over HTTPS to a DoH server
 * Resolve all InetAddress.getByName() calls securely and invisibly
 * Real benefit:
 * Secures DNS resolution from ISP snooping or man-in-the-middle attacks without touching OS settings, Docker, Kubernetes or network configs.
 */
//@formatter:on
public class Jep418Main {

  public static void main(String[] args) throws Exception {
    System.out.println("JEP 418 demo - custom InetAddress resolver\n");

    // This call should trigger our DemoInetAddressResolver
    InetAddress address = InetAddress.getByName("google.com");
    System.out.println("InetAddress.getByName(\"google.com\") -> " + address.getHostAddress());

    // Reverse lookup (also served by our resolver)
    String host = address.getHostName();
    System.out.println("Reverse hostname: " + host);

    // Build HTTP client
    HttpClient client = HttpClient.newHttpClient();

    // Build GET request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://google.com"))
        .GET()
        .build();

    System.out.println("Sending GET request...");
    // Try sending request (will hit 127.0.0.1 in our demo)
    //The request will fail because it will be send to localhost instead of google.com
    //If you just rename the META-INF folder then request it will work
    try {
      HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString());

      System.out.println("HTTP status: " + response.statusCode());
      System.out.println("Response body:\n" + response.body());

    } catch (Exception e) {
      System.out.println("HTTP request failed (expected in demo):");
      System.out.println(e.getClass().getSimpleName() + " -> " + e.getMessage());
    }
  }
}
