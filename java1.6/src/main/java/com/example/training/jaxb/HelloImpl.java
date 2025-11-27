package com.example.training.jaxb;// HelloImpl.java

import javax.jws.WebService;

@WebService(
    serviceName = "HelloService",
    portName = "HelloPort",
    endpointInterface = "com.example.training.jaxb.Hello",
    targetNamespace = "http://ws.example/"
)
public class HelloImpl implements Hello {

  public String greet(Person p) {
    if (p == null || p.getName() == null) {
      return "Hello!";
    }
    return "Hello, " + p.getName() + " (" + p.getAge() + ")";
  }

  public Person enrich(Person p) {
    if (p == null) {
      return new Person("Unknown", 0);
    }
    // pretend to enrich: cap min age at 1
    if (p.getAge() < 1) {
      p.setAge(1);
    }
    return p;
  }
}
