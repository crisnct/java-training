package com.example.training;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;

public class SslSessionCacheDemo {

  public static void main(String[] args) throws Exception {
    SSLContext ctx = SSLContext.getInstance("TLS");
    ctx.init(null, null, null);

    SSLSessionContext clientCtx = ctx.getClientSessionContext();

    System.out.println("Default session cache size: " + clientCtx.getSessionCacheSize());
    System.out.println("Default session timeout (seconds): " + clientCtx.getSessionTimeout());

    // You can still override them if you want:
    clientCtx.setSessionCacheSize(1000);
    clientCtx.setSessionTimeout(60);

    System.out.println("New session cache size: " + clientCtx.getSessionCacheSize());
    System.out.println("New session timeout (seconds): " + clientCtx.getSessionTimeout());
  }
}
