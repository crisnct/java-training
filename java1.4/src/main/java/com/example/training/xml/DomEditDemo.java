package com.example.training.xml;

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DomEditDemo {

  public static void main(String[] args) throws Exception {
    InputStream is = null;
    try {
      is = DomEditDemo.class.getResourceAsStream("../../../../books.xml");
      File out = new File("books_out.xml");   // rezultatul modificat

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      f.setValidating(false);
      DocumentBuilder b = f.newDocumentBuilder();
      Document doc = b.parse(is);

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
      System.out.println("Write: " + out.getAbsolutePath());
    } finally {
      if (is != null) {
        is.close();
      }
    }

  }
}
