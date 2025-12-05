# This project illustrates with small snippets the changes made in each Java version

Multi-module Maven build showing different Java targets.

- One module for each Java version
- See here all changes from each Java version https://gitmind.com/app/docs/m7ds9m1g 

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
