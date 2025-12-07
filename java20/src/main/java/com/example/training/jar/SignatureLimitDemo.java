package com.example.training.jar;

import java.io.IOException;
import java.util.jar.JarFile;

//@formatter:off
/**
 * Demonstrates Java 20 system property:
 * jdk.jar.maxSignatureFileSize
 *
 * Default limit: 8,000,000 bytes
 * Behavior:
 *  - Exceeding limit without property -> IOException
 *  - With property set high -> opens successfully
 */
//@formatter:on
public class SignatureLimitDemo {

    public static void main(String[] args) {
        String path = "large-sig.jar";
        String prop = System.getProperty("jdk.jar.maxSignatureFileSize");

        System.out.println("jar=" + path);
        System.out.println("jdk.jar.maxSignatureFileSize=" + prop);

        try (JarFile jar = new JarFile(path, true)) { // enable verification
            System.out.println("JAR opened successfully: " + jar.getName());
        } catch (IOException e) {
            System.out.println("FAILED: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }
    }
}
