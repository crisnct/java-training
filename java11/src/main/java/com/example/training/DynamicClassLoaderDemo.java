package com.example.training;

public class DynamicClassLoaderDemo {

    // This call looks like a normal static final constant.
    // But JVM may resolve it via ConstantDynamic (if compiled that way).
    private static final String VALUE = DynamicConstants.get();

    public static void main(String[] args) {
        System.out.println(VALUE);
    }
}

class DynamicConstants {
    public static String get() {
        // This method can be used by the JVM bootstrap mechanism
        // to lazily compute the constant.
        return "Dynamically computed constant";
    }
}
