package com.example.training;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.time.Instant;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/**

 //@formatter:off
 * Create jmod archive file
 * Execute this command in target/classes folder:
 * "C:\.....\temurin-25\bin\jmod.exe" create ^
 *   --class-path . ^
 *   --compress zip-9 ^
 *   "java20-demo.jmod"
 *   It will create java20-demo.jmod with best compression (9). The project must be modularized.
 *
 *   Ahead-of-Time class loading & linking (Leyden step, JEP 483)
 *   AOT demo
 *   {@snippet :
 *      Training run - record what classes get read/loaded/linked:
        java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -jar .\java24-1.0.0.jar
 *
 *      Create the cache (no app run, just builds the cache file):
 *      java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar .\java24-1.0.0.jar
 *
 *      Use the cache (faster startup):
 *      java -XX:AOTCache=app.aot -jar .\java24-1.0.0.jar
 *   }
 */
//@formatter:on
public class Application {

  static void main(String[] args) throws ParserConfigurationException, TransformerConfigurationException, NoSuchAlgorithmException {
    GreetingService greetingService = new DefaultGreetingService();
    GreetingPrinter printer = new GreetingPrinter(greetingService, System.out);
    String name = args.length > 0 ? args[0] : "World";
    printer.printGreeting(name);

    //Code that touches many providers at startup
    {
      long t0 = System.nanoTime();

      // Touch a bunch of subsystems that load/link many classes
      Map<String, Charset> cs = Charset.availableCharsets();
      if (cs.size() == 0) {
        throw new IllegalStateException("No charsets?");
      }
      DocumentBuilderFactory.newInstance().newDocumentBuilder();
      TransformerFactory.newInstance().newTransformer();
      "abc123XYZ".matches("(?i)[a-z0-9]+");         // init regex engine
      javax.net.ssl.SSLContext.getDefault();         // init JSSE
      new SecureRandom().nextBytes(new byte[32]);    // init JCA providers
      ImageIO.getReaderFormatNames();                // discover plugins
      Security.getProviders();                       // load all security providers

      long ms = (System.nanoTime() - t0) / 1_000_000;
      System.out.println("HeavyStartup completed in ~" + ms + " ms at " + Instant.now());
    }
  }
}
