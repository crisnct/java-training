package com.example.training.jaxb;// Publisher.java
import javax.xml.ws.Endpoint;

public class SoapPublisher {
    public static void main(String[] args) {
        String address = "http://localhost:8082/hello";
        Endpoint.publish(address, new HelloImpl());
        System.out.println("Service running at " + address);
        System.out.println("WSDL: " + address + "?wsdl");
        // Keep running
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {}
    }
}
