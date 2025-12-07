package com.example.training;

public class Unicode15Demo {

  public static void main(String[] args) {
    // U+1FAE0 is a Unicode 15 emoji: "Shaking Face"
    String emoji = "\uD83E\uDEE0"; // 🫠 would fail on older data, 🫠 is Unicode 14
    String pinkHeart = "\uD83E\uDE77"; // Unicode 15 pink heart

    int codePoint = pinkHeart.codePointAt(0);
    System.out.println("Char name: " + Character.getName(codePoint));

    String text = "Reaction: " + pinkHeart + " OK";
    System.out.println(text);

    // 1. Check the Unicode block (Java 20 knows about the script/emoji block)
    Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
    System.out.println("Block: " + block);

    // 2. Normalization using Unicode 15 data
    // If string uses new combining characters, NFC/NFD results differ on Java 20 vs older JDKs
    String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFC);
    System.out.println("Normalized: " + normalized);

    // 3. Regex: Java 20 Regex engine recognizes new Unicode 15 emoji properly
    // \p{Emoji} is not an official Java class, but "\X" matches extended grapheme clusters inclusively
    java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\X");
    java.util.regex.Matcher matcher = p.matcher(text);

    System.out.print("Grapheme clusters: ");
    while (matcher.find()) {
      System.out.print("[" + matcher.group() + "]");
    }
    System.out.println();
  }
}
