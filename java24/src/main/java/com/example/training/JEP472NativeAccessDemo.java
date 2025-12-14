package com.example.training;// JEP472NativeAccessDemo.java (Java 24+)
import java.lang.foreign.Linker;   // FFM API (also subject to the same policy in 24)

public class JEP472NativeAccessDemo {

    public static void main(String[] args) {
        System.out.println("java.version = " + System.getProperty("java.version"));

        // 1) JNI: loading a well-known system library (varies by OS)
        String lib = osIsWindows() ? "kernel32" : "c"; // libc on Unix-like
        try {
            System.out.println("JNI: System.loadLibrary(\"" + lib + "\")");
            System.loadLibrary(lib);   // Restricted native access -> warning (by default)
            System.out.println("JNI load succeeded.");
        } catch (Throwable t) {
            System.out.println("JNI load failed: " + t);
        }

        // 2) FFM: obtaining the native linker also emits the same style of warning in 24
        try {
            System.out.println("FFM: Linker.nativeLinker()");
            var linker = Linker.nativeLinker(); // Restricted native access -> warning (by default)
            System.out.println("FFM linker created: " + linker.getClass().getName());
        } catch (Throwable t) {
            System.out.println("FFM failed: " + t);
        }
    }

    private static boolean osIsWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
