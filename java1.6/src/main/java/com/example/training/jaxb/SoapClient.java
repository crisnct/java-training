package com.example.training.jaxb;// Client.java
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class SoapClient {
    public static void main(String[] args) throws Exception {
        URL wsdl = new URL("http://localhost:8082/hello?wsdl");
        QName serviceQN = new QName("http://ws.example/", "HelloService");
        QName portQN = new QName("http://ws.example/", "HelloPort");

        Service svc = Service.create(wsdl, serviceQN);
        Hello port = svc.getPort(portQN, Hello.class); // dynamic proxy against SEI

        Person you = new Person("Cristian", 43);
        String msg = port.greet(you);
        System.out.println("greet(): " + msg);

        Person enriched = port.enrich(new Person("Ana", 25));
        System.out.println("enrich(): " + enriched.getName() + " -> age=" + enriched.getAge());
    }
}
