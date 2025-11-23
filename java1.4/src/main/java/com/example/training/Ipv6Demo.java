package com.example.training;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Ipv6Demo {

  public static void main(String[] a) throws Exception {
    InetAddress[] all = InetAddress.getAllByName("ipv6.google.com");
    for (int i = 0; i < all.length; i++) {
      System.out.println(all[i].getHostAddress() + " | " + all[i].getClass().getName());
    }
    // Conectare la un host IPv6 (dacă e disponibil pe rețeaua ta)
    Socket s = new Socket();
    s.connect(new InetSocketAddress("::1", 8080), 3000); // localhost v6
    s.close();
  }
}
