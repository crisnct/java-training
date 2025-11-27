package com.example.training.jaxb;// Hello.java
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace = "http://ws.example/")
public interface Hello {
    @WebMethod
    String greet(Person p);

    @WebMethod
    Person enrich(Person p); // demonstrates JAXB object round-trip
}
