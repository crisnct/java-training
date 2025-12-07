package com.example.training.ssl;

import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

//@formatter:off
// Demo class: demonstrate Java 19 TLS signature scheme control using SSLSocket
// - You must target a real HTTPS server.
// - Use a real truststore, not trust-all.
//@formatter:on
public class SocketSignatureSchemesDemo {

  public static void main(String[] args) throws Exception {
    String host = "example.com";
    int port = 443;

    TrustManager[] trustManagers = loadSystemTrustManagers();

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagers, null);

    SSLSocketFactory factory = sslContext.getSocketFactory();

    try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {

      SSLParameters params = socket.getSSLParameters();

      // 1) WORKING signature schemes (example.com supports RSA-PSS)
      params.setSignatureSchemes(new String[]{
          "ecdsa_secp256r1_sha256",
          "ecdsa_secp384r1_sha384"
      });

      // 2) FAILING signature schemes (almost no server supports only this)
      // uncomment this to force a handshake failure:
//      params.setSignatureSchemes(new String[]{
//          "ecdsa_secp521r1_sha512"    // unrealistic-only config
//      });

      socket.setSSLParameters(params);

      System.out.println("Using signature schemes:");
      for (String scheme : params.getSignatureSchemes()) {
        System.out.println("  " + scheme);
      }

      System.out.println("\nStarting TLS handshake...");
      socket.startHandshake();
      System.out.println("Handshake successful — compatible signature schemes!");

    } catch (Exception e) {
      System.out.println("\nHandshake FAILED");
      e.printStackTrace();
    }
  }

  private static TrustManager[] loadSystemTrustManagers() throws Exception {
    TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init((KeyStore) null); // loads default cacerts
    return tmf.getTrustManagers();
  }
}
