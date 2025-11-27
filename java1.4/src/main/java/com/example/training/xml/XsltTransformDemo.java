package com.example.training.xml;

import java.io.InputStream;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XsltTransformDemo {
    public static void main(String[] args) throws Exception {
        InputStream books =  SaxParseDemo.class.getResourceAsStream("../../../../books.xml");
        InputStream template =  SaxParseDemo.class.getResourceAsStream("../../../../template.xsl");

        File out = new File("books.html");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer tr = tf.newTransformer(new StreamSource(template));
        tr.setParameter("title", "Book Catalog (XSLT 1.0)");
        tr.transform(new StreamSource(books), new StreamResult(out));

        System.out.println("HTML generated: " + out.getAbsolutePath());
    }
}
