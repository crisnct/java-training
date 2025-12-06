package com.example.training.spiRegistration;

import java.net.spi.InetAddressResolver;
import java.net.spi.InetAddressResolverProvider;

//@formatter:off
/**
 * Provider for DemoInetAddressResolver.
 *
 * This is what the JDK discovers via ServiceLoader
 * using the SPI file: META-INF/services/java.net.spi.InetAddressResolverProvider
 */
//@formatter:on
public class DemoInetAddressResolverProvider extends InetAddressResolverProvider {

    @Override
    public InetAddressResolver get(Configuration configuration) {
        System.out.println("[DemoResolverProvider] Providing DemoInetAddressResolver");
        return new DemoInetAddressResolver();
    }

    @Override
    public String name() {
        // Just an identifier for this provider
        return "demo-resolver";
    }
}
