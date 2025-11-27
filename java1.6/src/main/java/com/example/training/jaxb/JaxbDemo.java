package com.example.training.jaxb;// JaxbDemo.java
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class JaxbDemo {
    public static void main(String[] args) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Person.class);

        Person p = new Person("Mara", 28);

        StringWriter sw = new StringWriter();
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(p, sw);

        String xml = sw.toString();
        System.out.println("XML:\n" + xml);

        Unmarshaller u = ctx.createUnmarshaller();
        Person back = (Person) u.unmarshal(new StringReader(xml));
        System.out.println("Unmarshalled: " + back.getName() + " / " + back.getAge());
    }
}
