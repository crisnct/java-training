package com.example.training;

public class CompactStringsDemo {

    public static void main(String[] args) throws InterruptedException {
        int size = 500_000; // adjust as needed

        String latinSample   = "Hello World";      // Latin-1 only
        String unicodeSample = "你好 Cristian";     // non-Latin-1

        System.out.println("latinSample   = " + latinSample);
        System.out.println("unicodeSample = " + unicodeSample);

        Runtime rt = Runtime.getRuntime();

        // --- Latin-1 strings ---
        System.gc();
        Thread.sleep(500);
        long beforeLatin = usedMemory(rt);

        String[] latin = new String[size];
        for (int i = 0; i < size; i++) {
            latin[i] = latinSample + i; // different strings, avoid interning
        }

        System.gc();
        Thread.sleep(500);
        long afterLatin = usedMemory(rt);
        long latinUsed = afterLatin - beforeLatin;
        System.out.println("Approx memory for Latin-1 strings: " + latinUsed / 1024 + " KB");

        // --- Unicode strings ---
        System.gc();
        Thread.sleep(500);
        long beforeUnicode = usedMemory(rt);

        String[] unicode = new String[size];
        for (int i = 0; i < size; i++) {
            unicode[i] = unicodeSample + i;
        }

        System.gc();
        Thread.sleep(500);
        long afterUnicode = usedMemory(rt);
        long unicodeUsed = afterUnicode - beforeUnicode;
        System.out.println("Approx memory for Unicode strings: " + unicodeUsed / 1024 + " KB");

        /*
         * On Java 9+:
         *  - Latin strings use a compact 1-byte-per-char representation internally.
         *  - Non-Latin fall back to a 2-byte-per-char representation.
         * You should see Unicode consuming noticeably more heap.
         */
    }

    private static long usedMemory(Runtime rt) {
        return rt.totalMemory() - rt.freeMemory();
    }
}
