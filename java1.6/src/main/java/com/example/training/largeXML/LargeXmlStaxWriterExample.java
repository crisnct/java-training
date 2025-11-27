package com.example.training.largeXML;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LargeXmlStaxWriterExample {

    public static void main(String[] args) {
        String filePath = "products.xml";

        OutputStream outputStream = null;
        XMLStreamWriter xmlWriter = null;

        try {
            outputStream = new FileOutputStream(filePath);
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            xmlWriter = outputFactory.createXMLStreamWriter(outputStream, "UTF-8");

            writeProducts(xmlWriter);
            System.out.println("File generated: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeProducts(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("products");

        //Generates 77MB file
        for (int i = 1; i <= 999999; i++) {
            writer.writeStartElement("product");
            writer.writeAttribute("id", String.valueOf(i));

            writer.writeStartElement("name");
            writer.writeCharacters("Product " + i);
            writer.writeEndElement(); // </name>

            writer.writeStartElement("price");
            writer.writeCharacters(String.valueOf(10.0 * i));
            writer.writeEndElement(); // </price>

            writer.writeEndElement(); // </product>
        }


        writer.writeEndElement(); // </products>
        writer.writeEndDocument();
        writer.flush();
    }
}
