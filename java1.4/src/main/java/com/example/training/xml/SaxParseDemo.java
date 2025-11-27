package com.example.training.xml;

import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParseDemo {

  public static void main(String[] args) throws Exception {
    InputStream is =  SaxParseDemo.class.getResourceAsStream("../../../books.xml");

    SAXParserFactory f = SAXParserFactory.newInstance();
    f.setNamespaceAware(true);
    SAXParser p = f.newSAXParser();

    p.parse(is, new DefaultHandler() {
      private String current;
      private String id, title, author, price;

      public void startElement(String uri, String local, String qName, Attributes atts) {
        current = qName;
        if ("book".equals(qName)) {
          id = atts.getValue("id");
        }
      }

      public void characters(char[] ch, int start, int length) {
        if (current == null) {
          return;
        }
        String s = new String(ch, start, length).trim();
        if (s.length() == 0) {
          return;
        }
        if ("title".equals(current)) {
          title = s;
        }
        if ("author".equals(current)) {
          author = s;
        }
        if ("price".equals(current)) {
          price = s;
        }
      }

      public void endElement(String uri, String local, String qName) {
        if ("book".equals(qName)) {
          System.out.println("Book " + id + " — " + title + " / " + author + " — $" + price);
          id = title = author = price = null;
        }
        current = null;
      }
    });
  }
}
