package com.example.training;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//@formatter:off
/**
 * Java 20 DTLS resume-cookie demo (minimal and actually working).
 *
 * DTLS = TLS over UDP
 * A DTLS cookie is a little challenge token the server sends to the client before continuing the handshake.
 * Purpose:
 * stop attackers from spamming handshake packets using fake IPs
 * prevent DoS (Denial of Service) attacks
 *
 * What Java 20 changed:
 *   - DTLS now uses cookies not only for NEW handshakes, but also for
 *     RESUMED handshakes by default.
 *   - This behavior is controlled by the system property:
 *
 *       jdk.tls.enableDtlsResumeCookie
 *
 *     default = true (cookies used on both new + resumed DTLS handshakes)
 *
 * This class does NOT reimplement a full DTLS engine. Instead, it:
 *   - Shows the current value of jdk.tls.enableDtlsResumeCookie
 *   - Builds a DTLSv1.2 SSLContext (proving DTLS support is present)
 *   - Prints how you are supposed to run real DTLS code to see the effect
 *
 * How to run:
 *
 *   # 1) Default Java 20 behavior (cookies on new + resumed DTLS handshakes)
 *   java ^
 *     -Djdk.tls.enableDtlsResumeCookie=true ^
 *     -Djavax.net.debug=ssl,handshake,record ^
 *     -p target/classes ^
 *     -m com.example.training/com.example.training.DtlsCookiePropertyDemo
 *
 *   # 2) Old-style behavior (cookies only on NEW DTLS handshakes)
 *   java ^
 *     -Djdk.tls.enableDtlsResumeCookie=false ^
 *     -Djavax.net.debug=ssl,handshake,record ^
 *     -p target/classes ^
 *     -m com.example.training/com.example.training.DtlsCookiePropertyDemo
 *
 * Then:
 *   - Use the SAME flags on a real DTLS client/server (your app or a test tool).
 *   - In the debug output, check whether the RESUMED DTLS handshake still shows
 *     cookie/HelloVerifyRequest exchange or not.
 */
//@formatter:on
public class DtlsCookiePropertyDemo {

  public static void main(String[] args) throws Exception {
    printProperty();
    SSLContext dtlsContext = buildDtlsContext();
    printDtlsInfo(dtlsContext);
    printHowToUseInRealCode();
  }

  private static void printProperty() {
    String value = System.getProperty("jdk.tls.enableDtlsResumeCookie");
    System.out.println("jdk.tls.enableDtlsResumeCookie = " +
        (value == null ? "<not set> (default = true in Java 20+)" : value));
    System.out.println();
  }

  private static SSLContext buildDtlsContext() throws Exception {
    // Build a DTLSv1.2 context with a very permissive TrustManager
    // (for demo / local testing; do NOT use this in production).
    SSLContext ctx = SSLContext.getInstance("DTLSv1.2");
    TrustManager[] trustManagers = new TrustManager[]{
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(X509Certificate[] chain, String authType) {
          }

          @Override
          public void checkServerTrusted(X509Certificate[] chain, String authType) {
          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
    };
    ctx.init(null, trustManagers, new SecureRandom());
    return ctx;
  }

  private static void printDtlsInfo(SSLContext ctx) {
    SSLParameters supported = ctx.getSupportedSSLParameters();
    System.out.println("DTLS context created: " + ctx.getProtocol());
    System.out.println("Supported protocols: " + Arrays.toString(supported.getProtocols()));

    SSLParameters defaults = ctx.getDefaultSSLParameters();
    System.out.println("Default enabled protocols: " + Arrays.toString(defaults.getProtocols()));
    System.out.println();
  }

  private static void printHowToUseInRealCode() {
    String msg = """
        === How this property is actually used in practice ===
        
        In a real DTLS client/server, you do something like:
        
          SSLContext ctx = SSLContext.getInstance("DTLSv1.2");
          ctx.init(keyManagers, trustManagers, new SecureRandom());
          SSLEngine engine = ctx.createSSLEngine();
          engine.setUseClientMode(true or false);
        
        Then you run your app with:
          -Djdk.tls.enableDtlsResumeCookie=true   (new Java 20 behavior)
          -Djdk.tls.enableDtlsResumeCookie=false  (pre-Java 20 style)
        
        And enable TLS debug if you want proof:
          -Djavax.net.debug=ssl,handshake,record
        
        What you check in the debug logs:
          * true  -> cookies on NEW + RESUMED DTLS handshakes
          * false -> cookies only on NEW DTLS handshakes
        """;
    System.out.println(msg);
  }

}
