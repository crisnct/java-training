package com.example.training;

import java.text.BreakIterator;
import java.util.Locale;

//@formatter:off
// Demo class to show Unicode 14.0 support (Character.getName, UnicodeScript, BreakIterator)
//@formatter:on
public class Unicode14Demo {

  public static void main(String[] args) {
    // Unicode 14 emoji: MELTING FACE (U+1FAE0)
    int meltingFace = 0x1FAE0;

    // Unicode 14 script: VITHKUQI CAPITAL LETTER A (U+10570)
    int vithkuqiA = 0x10570;

    String meltingFaceStr = new String(Character.toChars(meltingFace));
    String vithkuqiStr = new String(Character.toChars(vithkuqiA));

    System.out.println("=== Unicode 14 code points ===");
    System.out.println("Melting face char: " + meltingFaceStr);
    System.out.println("Melting face name: " + Character.getName(meltingFace));
    System.out.println("Melting face type: " + Character.getType(meltingFace));

    System.out.println();
    System.out.println("Vithkuqi char: " + vithkuqiStr);
    System.out.println("Vithkuqi name: " + Character.getName(vithkuqiA));
    System.out.println("Vithkuqi script: " + Character.UnicodeScript.of(vithkuqiA));
    System.out.println("Vithkuqi isAlphabetic: " + Character.isAlphabetic(vithkuqiA));

    // BreakIterator working on a string containing new Unicode 14 characters
    String text = "Hello " + vithkuqiStr + " world " + meltingFaceStr;
    System.out.println();
    System.out.println("Text: " + text);
    System.out.println("Word boundaries via BreakIterator:");

    BreakIterator wordIterator = BreakIterator.getWordInstance(Locale.ENGLISH);
    wordIterator.setText(text);

    String[] parts = text.split(" ");

    int start = wordIterator.first();
    for (int end = wordIterator.next(); end != BreakIterator.DONE; start = end, end = wordIterator.next()) {
      String word = text.substring(start, end);
      // Skip pure whitespace tokens to keep output clean
      if (!word.trim().isEmpty()) {
        System.out.println("[" + word + "]");
      }
    }
  }
}
