package com.example.training;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Root Certificates (Default CA Set)
 *
 * • Ships the JDK with a default set of root Certification Authority (CA) certificates
 * • Eliminates the need to manually import common root CAs in many environments
 * • Improves out-of-the-box support for TLS/HTTPS connections
 * • Reduces configuration friction for secure networked applications
 */
public class HttpsDemo {

    public static void main(String[] args) throws Exception {
        String httpsUrl = "https://api.github.com";

        URL url = new URL(httpsUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // Trigger the connection
        int code = connection.getResponseCode();

        System.out.println("Protocol:       " + connection.getURL().getProtocol());
        System.out.println("Response Code:  " + code);
        System.out.println("Cipher Suite:   " + connection.getCipherSuite());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {

            String line;
            System.out.println("---- Response Body ----");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
