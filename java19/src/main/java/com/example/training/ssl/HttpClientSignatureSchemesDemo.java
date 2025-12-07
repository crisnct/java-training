package com.example.training.ssl;

import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

//@formatter:off
/**
 * Example: Using Java HttpClient with custom TLS signature schemes (Java 19 feature)
 * Before running you need to do this:
 * {@snippet :
 * keytool -printcert -rfc -sslserver example.com:443 > server.pem
 * keytool -importcert -trustcacerts -file server.pem \
 *     -keystore "c:\Users\neluc\.jdks\temurin-25\bin\mykeystore.jks" \
 *     -alias target-server -storepass dnadna
 * }
 */
//@formatter:on
public class HttpClientSignatureSchemesDemo {

  private static TrustManager[] loadTrustManagers(String truststorePath, String password) throws Exception {
    KeyStore trustStore = KeyStore.getInstance("JKS");
    try (FileInputStream fis = new FileInputStream(truststorePath)) {
      trustStore.load(fis, password.toCharArray());
    }
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStore);

    return tmf.getTrustManagers();
  }

  public static void main(String[] args) throws Exception {
    // 1. Create a permissive TrustManager (demo only)
    //not good for prod:
    //TrustManager[] trustAll = new TrustManager[]{new InsecureTrustAllManager()};
    TrustManager[] trustManagers = loadTrustManagers(
        "c:/Users/neluc/.jdks/temurin-25/bin/mykeystore.jks",
        System.getenv("KEYSTORE_PWD")
    );
    //Default:
//    TrustManagerFactory tmf =
//        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//    tmf.init((KeyStore) null); // use default cacerts
//    TrustManager[] trustManagers = tmf.getTrustManagers();

    // 2. Create TLS context
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagers, null);

    // 3. Configure SSLParameters with custom signature schemes
    SSLParameters sslParameters = new SSLParameters();
    sslParameters.setSignatureSchemes(new String[]{
        "ecdsa_secp256r1_sha256",
        "ecdsa_secp384r1_sha384"
    });

    // IMPORTANT:
    // We set default SSLParameters for socket engines created by this SSLContext.
    sslContext.getDefaultSSLParameters().setSignatureSchemes(sslParameters.getSignatureSchemes());
    sslContext.getSupportedSSLParameters().setSignatureSchemes(sslParameters.getSignatureSchemes());

    // 4. Build HttpClient with the configured SSLContext
    HttpClient client = HttpClient.newBuilder()
        .sslContext(sslContext)
        .build();

    // 5. Standard GET request
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://example.com/"))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("HTTP status: " + response.statusCode());
    System.out.println("Body:");
    System.out.println(response.body());
  }

}
