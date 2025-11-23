package com.example.training;

public class AssertDemo {

    public static void main(String[] args) {
        // -------- 1) assert keyword (Java 1.4) --------
        int items = computeItems();
        assert items >= 0 : "Invariant failed: items must be non-negative, got " + items;

        // simple assert (no message)
        assert isPowerOfTwo(8);

        System.out.println("Assertions passed (if enabled). Now demonstrating chained exceptions…");

        // -------- 2) Chained exceptions (Java 1.4) --------
        try {
            serviceLayer();
        } catch (RuntimeException e) {
            System.out.println("Caught top-level exception: " + e);
            // Print cause chain using getCause()
            Throwable t = e;
            while ((t = t.getCause()) != null) {
                System.out.println("  caused by: " + t);
            }
        }
    }

    // Simulate a small stack of calls that wrap causes
    private static void serviceLayer() {
        try {
            repositoryLayer();
        } catch (IllegalStateException cause) {
            // Java 1.4 added (String, Throwable) constructors:
            throw new RuntimeException("Service failed", cause);
        }
    }

    private static void repositoryLayer() {
        try {
            parseConfigValue("not-a-number");
        } catch (NumberFormatException original) {
            // Alternative: initCause(...) if your exception type lacks (msg, cause)
            IllegalStateException wrapped = new IllegalStateException("Bad repository state");
            wrapped.initCause(original); // Java 1.4 API
            throw wrapped;
        }
    }

    private static int parseConfigValue(String s) {
        // will throw NumberFormatException
        return Integer.parseInt(s);
    }

    // ----- tiny helpers for assert demo -----
    private static int computeItems() {
        // Put your dev-time checks here; return a valid non-negative value in prod
        return 3;
    }

    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}
