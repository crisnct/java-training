package com.example.training.spiRegistration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.spi.InetAddressResolver;
import java.util.stream.Stream;

//@formatter:off
/**
 * Custom resolver for JEP 418 demo.
 *
 * - Any hostname is resolved to 127.0.0.1
 * - Reverse lookup always returns "demo.local"
 *
 * This class shows how to implement InetAddressResolver.
 */
//@formatter:on
public class DemoInetAddressResolver implements InetAddressResolver {

  @Override
  public Stream<InetAddress> lookupByName(String host, LookupPolicy lookupPolicy)
      throws UnknownHostException {
    // In a real resolver you would:
    // - honor the lookupPolicy (IPV4, IPV6, etc.)
    // - call DNS-over-HTTPS or a corporate DNS
    // Here we just demo the mechanism.
    InetAddress address = InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
    System.out.println("[DemoResolver] lookupByName(" + host + ") -> " + address.getHostAddress());
    return Stream.of(address);
  }

  @Override
  public String lookupByAddress(byte[] addr) {
    // Reverse lookup demo
    String result = "demo.local";
    System.out.println("[DemoResolver] lookupByAddress(" + addr[0] + "." + addr[1] + "." + addr[2] + "." + addr[3] + ") -> " + result);
    return result;
  }
}
