package com.example.training;

import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

//@formatter:off
/**
 * Java 20 demo for (D)TLS named groups:
 * - SSLParameters.getNamedGroups()
 * - SSLParameters.setNamedGroups(String[])
 *
 * Shows how to:
 *  1) Read the default named groups
 *  2) Restrict them to specific groups for this connection only
 */
//@formatter:on
public class NamedGroupsDemo {

  public static void main(String[] args) throws Exception {
    SSLContext context = SSLContext.getDefault();

    // 1) See which named groups this JDK supports
    SSLParameters supported = context.getSupportedSSLParameters();
    System.out.println("Supported named groups: " +
        Arrays.toString(supported.getNamedGroups()));

    // 2) Take default parameters for a (D)TLS connection
    SSLParameters params = context.getDefaultSSLParameters();
    System.out.println("Default named groups:   " +
        Arrays.toString(params.getNamedGroups()));

    // 3) Restrict to specific groups for this connection
    //    (names must come from the supported list above)
    params.setNamedGroups(new String[]{"x25519", "secp256r1"});

    // 4) Apply to an SSLEngine (per-connection configuration)
    SSLEngine engine = context.createSSLEngine();
    engine.setSSLParameters(params);

    System.out.println("Configured named groups:" +
        Arrays.toString(engine.getSSLParameters().getNamedGroups()));
  }
}
