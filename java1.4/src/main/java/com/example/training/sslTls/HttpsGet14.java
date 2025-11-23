package com.example.training.sslTls;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpsGet14 {

  public static void main(String[] args) throws Exception {
    // Timeouts pentru Java 1.4 (prin proprietăți de sistem)
    System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
    System.setProperty("sun.net.client.defaultReadTimeout", "8000");

    String url = "https://example.com/"; // schimbă după nevoie
    HttpsURLConnection c = null;
    InputStream in = null;
    try {
      c = (HttpsURLConnection) new URL(url).openConnection();
      c.setRequestProperty("User-Agent", "HttpsGet14/1.0");
      c.setRequestProperty("Connection", "close"); // închide conexiunea după răspuns

      in = c.getInputStream();
      byte[] buf = new byte[8192];
      int n;
      ByteArrayOutputStream out = new ByteArrayOutputStream(16384);
      while ((n = in.read(buf)) != -1) {
        out.write(buf, 0, n);
      }
      System.out.println(new String(out.toByteArray(), "ISO-8859-1"));
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception ignored) {
        }
      }
      if (c != null) {
        try {
          c.disconnect();
        } catch (Exception ignored) {
        }
      }
    }
  }
}
