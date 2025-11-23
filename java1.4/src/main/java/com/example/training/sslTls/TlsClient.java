package com.example.training.sslTls;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.InputStream;
import java.io.OutputStream;

public class TlsClient {
  public static void main(String[] args) throws Exception {
    String host = "metal-investment-635786220311.europe-west1.run.app";
    int port = 443;

    SSLSocket s = null;
    try {
      SSLContext sc = SSLContext.getInstance("TLS"); // pe 1.4 înseamnă TLSv1
      sc.init(null, null, null);
      SSLSocketFactory f = sc.getSocketFactory();
      s = (SSLSocket) f.createSocket(host, port);

      // Opțional: forțează explicit TLSv1 (1.4 nu știe TLSv1.2+)
      // s.setEnabledProtocols(new String[] { "TLSv1" });

      s.startHandshake();

      // HTTP/1.1 corect: include Host și Connection: close
      String req =
          "GET / HTTP/1.1\r\n" +
          "Host: " + host + "\r\n" +
          "Connection: close\r\n" +
          "User-Agent: SimpleTLSClient14/1.0\r\n\r\n";

      OutputStream out = s.getOutputStream();
      out.write(req.getBytes("US-ASCII"));
      out.flush();

      InputStream in = s.getInputStream();
      byte[] buf = new byte[8192];
      int n;
      while ((n = in.read(buf)) != -1) {
        System.out.write(buf, 0, n); // răspunsul brut (headers + body)
      }
    } finally {
      if (s != null) try { s.close(); } catch (Exception ignore) {}
    }
  }
}
