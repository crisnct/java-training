package com.example.training;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class ListRootCAs {

  public static void main(String[] args) throws Exception {
    // 1. Determine which trust store is actually in use
    String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
    if (trustStorePath == null) {
      String javaHome = System.getProperty("java.home");
      trustStorePath = javaHome + File.separator + "lib"
          + File.separator + "security"
          + File.separator + "cacerts";
    }

    String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    if (trustStorePassword == null) {
      // Default password for JDK cacerts (unless changed)
      trustStorePassword = "changeit";
    }

    System.out.println("Trust store path: " + trustStorePath);

    // 2. Load the keystore from the trust store file
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    try (InputStream is = new FileInputStream(trustStorePath)) {
      ks.load(is, trustStorePassword.toCharArray());
    }

    // 3. Enumerate all aliases and print certificate subjects
    Enumeration<String> aliases = ks.aliases();
    if (!aliases.hasMoreElements()) {
      System.out.println("No entries found in trust store.");
      return;
    }

    System.out.println("Trusted root CAs / cert entries:");
    while (aliases.hasMoreElements()) {
      String alias = aliases.nextElement();
      X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
      if (cert != null) {
        System.out.println(alias + " : " + cert.getSubjectX500Principal());
      }
    }
  }
}
