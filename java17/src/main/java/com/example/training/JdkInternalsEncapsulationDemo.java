package com.example.training;// Compile with: javac JdkInternalsEncapsulationDemo.java

//@formatter:off
/**
 * Run on Java 17+ WITHOUT extra flags to see InaccessibleObjectException.
 * Strong encapsulation of JDK internals JEP 403 – Strongly Encapsulate JDK
 * Internals By default, almost all sun.* and other internal packages are fully encapsulated;
 * only a few are still accessible for backward compat.
 * --illegal-access is effectively done;
 * you now need --add-opens / --add-exports explicitly if you cheat. Then run again WITH: java --add-opens
 * java.base/sun.security.x509=ALL-UNNAMED JdkInternalsEncapsulationDemo
 */
//@formatter:on
public class JdkInternalsEncapsulationDemo {

  public static void main(String[] args) throws Exception {
    // Internal JDK class from an internal package
    Class<?> x500NameClass = Class.forName("sun.security.x509.X500Name");

    // Non-public constructor: X500Name(String name)
    var ctor = x500NameClass.getDeclaredConstructor(String.class);

    // Java 17 (JEP 403): this line will throw
    // java.lang.reflect.InaccessibleObjectException
    // unless you use --add-opens.
    ctor.setAccessible(true);

    Object x500Name = ctor.newInstance("CN=Test");
    System.out.println("Created internal object: " + x500Name);
  }
}
