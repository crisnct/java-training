package com.example.training;

import java.util.Objects;

public class ObjectsUtilDemo {

    public static void main(String[] args) {
        Person p1 = new Person("John", 30);
        Person p2 = new Person("John", 30);

        System.out.println("p1 equals p2: " + p1.equals(p2));
        System.out.println("p1 hashCode:  " + p1.hashCode());
        System.out.println("p2 hashCode:  " + p2.hashCode());
    }

    static class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            // Throws NullPointerException with a clear message if name is null
            this.name = Objects.requireNonNull(name, "name must not be null");
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Person person = (Person) o;
            return age == person.age &&
                   Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }
}
