package com.example.training;
//@formatter:off
/*
 * Java 17+ example – Context-specific deserialization filters (JEP 415)
 * Allows setting filters based on context (e.g. per framework / per stream) to harden ObjectInputStream and friends against deserialization gadget attacks.
 * Idea:
 * - We have a "tenant" context stored in a ThreadLocal (user / admin).
 * - For each ObjectInputStream we choose a different ObjectInputFilter based on that context.
 * - Result:
 *      - "user" is allowed to deserialize only java.lang.String
 *      - "admin" is allowed to deserialize java.lang.String and java.lang.Integer
 *
 * This demonstrates JEP 415's core concept:
 *   filters can be chosen "per context" instead of one global static policy.
 */
//@formatter:on

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ContextSpecificFilterDemo {

    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        byte[] stringData = serialize("hello");
        byte[] intData = serialize(Integer.valueOf(42));

        System.out.println("=== user context ===");
        deserializeForTenant("user", stringData);   // OK
        try {
            deserializeForTenant("user", intData);  // should be rejected
        } catch (InvalidClassException e) {
            System.out.println("user cannot deserialize Integer: " + e);
        }

        System.out.println("\n=== admin context ===");
        deserializeForTenant("admin", stringData);  // OK
        deserializeForTenant("admin", intData);     // OK
    }

    private static byte[] serialize(Object value) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(value);
            oos.flush();
            return baos.toByteArray();
        }
    }

    private static Object deserializeForTenant(String tenant, byte[] data) throws Exception {
        TENANT.set(tenant);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            // Context-specific filter (this is the "JEP 415" idea in practice)
            ObjectInputFilter filter = selectFilterForTenant(tenant);
            ois.setObjectInputFilter(filter);

            Object result = ois.readObject();
            System.out.println(tenant + " deserialized: " + result + " (" + result.getClass().getName() + ")");
            return result;
        } finally {
            TENANT.remove();
        }
    }

    private static ObjectInputFilter selectFilterForTenant(String tenant) {
        return switch (tenant) {
            case "admin" ->
                    // Allow anything in java.base, reject others.
                    // This includes String, Integer, etc.
                    //createFilter was introduced in java 17
                    ObjectInputFilter.Config.createFilter("java.base/*;!*");
            case "user" ->
                    // Allow only java.lang.String explicitly, everything else rejected.
                    ObjectInputFilter.Config.createFilter("java.lang.String;!*");
            default ->
                    // Reject everything by default.
                    ObjectInputFilter.Config.createFilter("!*");
        };
    }
}
