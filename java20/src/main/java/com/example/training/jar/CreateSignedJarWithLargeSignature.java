package com.example.training.jar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

//@formatter:off
/**
 * Creates a fake signed JAR with a deliberately large signature file
 * to trigger the Java 20 limit: jdk.jar.maxSignatureFileSize
 */
//@formatter:on
public class CreateSignedJarWithLargeSignature {

    public static void main(String[] args) throws Exception {
        Path jar = Path.of("large-sig.jar");
        createJarWithLargeSignature(jar, 100_000_000); // 10 MB sig file
        System.out.println("Created: " + jar.toAbsolutePath());
    }

    private static void createJarWithLargeSignature(Path jarPath, int size) throws IOException {
        byte[] payload = new byte[size];
        for (int i = 0; i < size; i++) {
            payload[i] = (byte) (i % 256);
        }

        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarPath.toFile()))) {
            // Normal class entry
            jos.putNextEntry(new JarEntry("com/example/Demo.class"));
            jos.write(new byte[] {0,1,2,3});
            jos.closeEntry();

            // "Signature file" entry
            // Real signature files live under META-INF/*.SF, *.DSA, *.RSA
            jos.putNextEntry(new JarEntry("META-INF/BIG.SF"));
            jos.write(payload);
            jos.closeEntry();
        }
    }
}
