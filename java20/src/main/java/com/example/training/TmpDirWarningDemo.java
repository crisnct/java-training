package com.example.training;

public class TmpDirWarningDemo {

    //@formatter:off
    /**
     * Demonstrates the Java 20 warning:
     * If -Djava.io.tmpdir points to a non-existent directory,
     * the JVM prints a startup warning:
     *
     *   WARNING: java.io.tmpdir directory does not exist: X:\no\such\dir
     */
    //@formatter:on
    public static void main(String[] args) {
        String tmp = System.getProperty("java.io.tmpdir");
        System.out.println("java.io.tmpdir = " + tmp);
    }
}
