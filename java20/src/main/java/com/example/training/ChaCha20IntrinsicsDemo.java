package com.example.training;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.ChaCha20ParameterSpec;

//@formatter:off
/**
 * Demonstrates the performance impact of ChaCha20 intrinsics in Java 20+.
 * ChaCha20 is a stream cipher. It’s a way of encrypting data so nobody can read it without the key
 *
 * Run with intrinsics (default ON):
 *   java -p target/classes -m com.example.training/com.example.training.ChaCha20IntrinsicsDemo
 *
 * Run with intrinsics disabled:
 *   java -XX:+UnlockDiagnosticVMOptions -XX:-UseChaCha20Intrinsics ^
 *        -p target/classes ^
 *        -m com.example.training/com.example.training.ChaCha20IntrinsicsDemo
 *
 * Compare the "throughput: ..." values.
 */
//@formatter:on
public class ChaCha20IntrinsicsDemo {

  private static final int DATA_SIZE_BYTES = 100 * 1024 * 1024; // 100 MB

  private static final int ROUNDS = 5;

  public static void main(String[] args) throws Exception {
    System.out.println("Using ChaCha20 via SunJCE.");
    System.out.println("Data size per round: " + (DATA_SIZE_BYTES / (1024 * 1024)) + " MB");
    System.out.println("Rounds: " + ROUNDS);

    SecretKey key = generateChaChaKey();
    byte[] baseNonce = generateNonce();

    byte[] plain = new byte[DATA_SIZE_BYTES];
    new SecureRandom().nextBytes(plain);

    Cipher cipher = Cipher.getInstance("ChaCha20");

    Instant start = Instant.now();

    long totalBytes = 0;
    //Encrypts multiple times
    for (int i = 0; i < ROUNDS; i++) {
      byte[] nonceForRound = nextNonce(baseNonce, i);
      byte[] cipherText = encryptOnce(cipher, key, nonceForRound, plain);
      totalBytes += cipherText.length;
    }

    Instant end = Instant.now();
    Duration elapsed = Duration.between(start, end);

    double seconds = elapsed.toMillis() / 1000.0;
    double mbProcessed = totalBytes / (1024.0 * 1024.0);

    //without these arguments throughput is higher
    //-XX:+UnlockDiagnosticVMOptions -XX:-UseChaCha20Intrinsics
    System.out.printf("Elapsed: %.3f s, processed: %.1f MB, throughput: %.1f MB/s%n",
        seconds, mbProcessed, mbProcessed / seconds);
  }

  private static SecretKey generateChaChaKey() throws Exception {
    KeyGenerator kg = KeyGenerator.getInstance("ChaCha20");
    kg.init(256);
    return kg.generateKey();
  }

  private static byte[] generateNonce() {
    byte[] nonce = new byte[12]; // 96-bit nonce
    new SecureRandom().nextBytes(nonce);
    return nonce;
  }

  private static byte[] nextNonce(byte[] baseNonce, int round) {
    byte[] copy = Arrays.copyOf(baseNonce, baseNonce.length);
    // simple tweak so nonce differs each round
    int lastIndex = copy.length - 1;
    copy[lastIndex] = (byte) (copy[lastIndex] + round);
    return copy;
  }

  private static byte[] encryptOnce(Cipher cipher, SecretKey key, byte[] nonce, byte[] data) throws Exception {
    ChaCha20ParameterSpec spec = new ChaCha20ParameterSpec(nonce, 1);
    cipher.init(Cipher.ENCRYPT_MODE, key, spec);
    return cipher.doFinal(data);
  }
}
