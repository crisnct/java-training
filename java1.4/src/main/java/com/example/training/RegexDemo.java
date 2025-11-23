package com.example.training;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexDemo {

  public static void main(String[] args) {
    // 1) Match + grupuri: email simplu (demo, nu e validare completă)
    String email = "User.Name+tag@example-domain.com";
    Pattern pEmail = Pattern.compile("^([A-Za-z0-9._%+-]+)@([A-Za-z0-9.-]+)$");
    Matcher mEmail = pEmail.matcher(email);
    if (mEmail.matches()) {
      System.out.println("Local: " + mEmail.group(1));
      System.out.println("Domain: " + mEmail.group(2));
    }

    // 2) find(): extrage toate numerele din text (cu MULTILINE + CASE_INSENSITIVE)
    String text = "Order A12\nTotal: 345 RON\nRef: x9y";
    Pattern pNum = Pattern.compile("([0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    Matcher mNum = pNum.matcher(text);
    System.out.print("Numere găsite: ");
    boolean first = true;
    while (mNum.find()) {
      if (!first) {
        System.out.print(", ");
      }
      System.out.print(mNum.group(1));
      first = false;
    }
    System.out.println();

    // 3) replaceAll (Java 1.4): normalizează spațiile multiple
    String messy = "Java   1.4   regex\t demo";
    String normalized = messy.replaceAll("\\s+", " ");
    System.out.println("Before: [" + messy + "]");
    System.out.println("After : [" + normalized + "]");

    // 4) Non-greedy + DOTALL: capturăm cel mai scurt bloc dintre tag-uri
    String html = "<p>first</p><p>second</p>";
    Pattern pTag = Pattern.compile("<p>(.*?)</p>", Pattern.DOTALL);
    Matcher mTag = pTag.matcher(html);
    while (mTag.find()) {
      System.out.println("Tag content: " + mTag.group(1));
    }
  }
}
