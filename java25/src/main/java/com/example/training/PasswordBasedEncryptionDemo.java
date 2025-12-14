//@formatter:off
/**
 * Demonstrates password-based AES encryption and decryption using
 * Java 25's standardized PBKDF2-HMAC-SHA256 key derivation (JEP 484).
 *
 * Steps:
 *  1. Derive a 256-bit AES key from a password via PBKDF2.
 *  2. Encrypt plaintext with AES/GCM/NoPadding.
 *  3. Decrypt ciphertext back to verify correctness.
 *
 * Notes:
 *  - AES/GCM provides both encryption and authentication.
 *  - A random salt + IV must be generated for every encryption.
 *  - Store salt + IV alongside ciphertext to enable decryption.
 *  - Never reuse the same (key + IV) pair for multiple messages.
 */
//@formatter:on
package com.example.training;

import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordBasedEncryptionDemo {

  private static final int SALT_LEN = 16;
  private static final int IV_LEN = 12;
  private static final int ITERATIONS = 150_000;
  private static final int KEY_BITS = 256;
  private static final int GCM_TAG_BITS = 128;

  static void main() throws Exception {
    String password = "S3cur3P@ss!";
    byte[] salt = randomBytes(SALT_LEN);
    byte[] iv = randomBytes(IV_LEN);

    // Derive key from password
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_BITS);
    byte[] keyBytes = factory.generateSecret(spec).getEncoded();
    SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

    System.out.println("Derived AES key: " + hex(aesKey.getEncoded()));
    System.out.println("Salt: " + hex(salt));
    System.out.println("IV: " + hex(iv));

    // Encrypt some plaintext
    Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
    encryptCipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
    byte[] encrypted = encryptCipher.doFinal(password.getBytes());
    System.out.println("\nEncrypted: " + hex(encrypted));

    // Decrypt to verify
    Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
    decryptCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
    byte[] decrypted = decryptCipher.doFinal(encrypted);
    System.out.println("Decrypted: " + new String(decrypted));
  }

  private static byte[] randomBytes(int len) {
    byte[] b = new byte[len];
    new SecureRandom().nextBytes(b);
    return b;
  }

  private static String hex(byte[] b) {
    return HexFormat.of().formatHex(b);
  }
}
