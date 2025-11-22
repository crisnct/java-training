# java-training

Multi-module Maven build showing different Java targets.

- `java6`: original greeting console app compiled for Java 6. Tests are skipped here because JUnit 5 requires Java 8+. You need a JDK 6 toolchain to build this module.
- `java17`: messaging components compiled for Java 17 with JUnit 5 + Mockito tests.

## Building

- Modern module only: `mvn -pl modern-java17 test`
- All modules (requires JDK 6 available via toolchains): `mvn test`

Example `~/.m2/toolchains.xml` entry for the Java 6 module:

```xml
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>1.6</version>
    </provides>
    <configuration>
      <jdkHome>C:\Path\To\jdk6</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```
