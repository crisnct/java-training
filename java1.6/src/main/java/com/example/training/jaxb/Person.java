package com.example.training.jaxb;// Person.java
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "person")
public class Person {
    private String name;
    private int age;

    // JAXB requires a no-arg constructor
    public Person() {}

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @XmlElement
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @XmlElement
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
