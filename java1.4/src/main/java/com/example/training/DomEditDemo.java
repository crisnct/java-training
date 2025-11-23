package com.example.training;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class DomEditDemo {
    public static void main(String[] args) throws Exception {
        File in  = new File("books.xml");       // ex: /mnt/data/books.xml
        File out = new File("books_out.xml");   // rezultatul modificat

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        Document doc = b.parse(in);

        // Exemplu: crește prețul cu 10% pentru categoria "programming"
        NodeList books = doc.getElementsByTagName("book");
        for (int i = 0; i < books.getLength(); i++) {
            Element book = (Element) books.item(i);
            String category = book.getAttribute("category");
            if ("programming".equals(category)) {
                Element priceEl = (Element) book.getElementsByTagName("price").item(0);
                String txt = priceEl.getFirstChild().getNodeValue();
                double val = Double.valueOf(txt).doubleValue();
                val = Math.round(val * 1.10 * 100.0) / 100.0;
                priceEl.getFirstChild().setNodeValue(String.valueOf(val));
            }
        }

        // Scrie arborele înapoi pe disc
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty("indent", "yes");
        t.transform(new DOMSource(doc), new StreamResult(out));
        System.out.println("Scris: " + out.getAbsolutePath());
    }
}
