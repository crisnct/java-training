package com.example.training;

import java.nio.charset.Charset;

public class EncodingDemo {

  public static void main(String[] args) {

    // Show detected encodings for stdout and stderr
    Charset outCharset = Charset.forName(System.getProperty("stdout.encoding"));
    Charset errCharset = Charset.forName(System.getProperty("stderr.encoding"));

    System.out.println("Detected stdout encoding: " + outCharset);
    System.err.println("Detected stderr encoding: " + errCharset);

    // Example output containing non-ASCII characters
    System.out.println("System.out UTF-8 test: România — șțîă");
    System.err.println("System.err UTF-8 test: München — äöüß");
  }
}
