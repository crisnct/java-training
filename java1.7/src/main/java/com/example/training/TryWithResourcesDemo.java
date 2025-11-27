package com.example.training;

public class TryWithResourcesDemo {

    public static void main(String[] args) {
        new TryWithResourcesDemo().demo();
    }

    public void demo() {
        try (MyResource resource = new MyResource()) {
            resource.work();
        } catch (Exception e) {
            System.out.println("Handled exception: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------

    static class MyResource implements AutoCloseable {

        public void work() {
            System.out.println("Doing some work...");
        }

        @Override
        public void close() {
            System.out.println("Resource closed automatically.");
        }
    }
}
