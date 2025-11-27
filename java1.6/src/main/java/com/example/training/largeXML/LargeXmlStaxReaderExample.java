package com.example.training.largeXML;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LargeXmlStaxReaderExample {

  public static void main(String[] args) {
    String filePath = "products.xml";

    InputStream inputStream = null;
    XMLStreamReader xmlReader = null;

    try {
      inputStream = new FileInputStream(filePath);

      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      xmlReader = inputFactory.createXMLStreamReader(inputStream, "UTF-8");

      processProducts(xmlReader);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    } finally {
      if (xmlReader != null) {
        try {
          xmlReader.close();
        } catch (XMLStreamException e) {
          e.printStackTrace();
        }
      }
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void processProducts(XMLStreamReader xmlReader) throws XMLStreamException {
    String currentElement = null;
    String productId = null;
    String productName = null;
    String productPrice = null;

    while (xmlReader.hasNext()) {
      int eventType = xmlReader.next();

      switch (eventType) {
        case XMLStreamConstants.START_ELEMENT:
          currentElement = xmlReader.getLocalName();

          if ("product".equals(currentElement)) {
            productId = xmlReader.getAttributeValue(null, "id");
            productName = null;
            productPrice = null;
          }
          break;

        case XMLStreamConstants.CHARACTERS:
          if (currentElement == null) {
            break;
          }
          String text = xmlReader.getText().trim();
          if (text.length() == 0) {
            break;
          }

          if ("name".equals(currentElement)) {
            productName = text;
          } else if ("price".equals(currentElement)) {
            productPrice = text;
          }
          break;

        case XMLStreamConstants.END_ELEMENT:
          String endElement = xmlReader.getLocalName();
          if ("product".equals(endElement)) {
            System.out.println("Product: id=" + productId
                + ", name=" + productName
                + ", price=" + productPrice);
          }
          currentElement = null;
          break;

        default:
          break;
      }
    }
  }
}
