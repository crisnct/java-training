package com.example.training;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KEM;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//@formatter:off
/**
 * Java 21 – Key Encapsulation Mechanism (JEP 452) end-to-end demo.
 *
 * Flow:
 *  1) Receiver generates an X25519 key pair (public shared with sender).
 *  2) Sender runs KEM(DHKEM).encapsulate(receiverPub) -> (sharedSecret, encapsulationBytes[, params])
 *  3) Sender derives an AES key from sharedSecret, encrypts with AES/GCM.
 *  4) Sender sends {ciphertext, iv, encapsulationBytes[, params]} to receiver.
 *  5) Receiver runs KEM(DHKEM).decapsulate(encapsulationBytes) -> same sharedSecret,
 *     derives the same AES key, and decrypts.
 *
 * Notes:
 *  - Algorithm name "DHKEM" is standard; X25519 keys are supported in JDK 21.
 *  - For demo key derivation we hash the shared secret with SHA-256 and take 16 bytes for AES-128.
 *    In production, use a proper KDF (e.g., HKDF) and include context info.
 */
//@formatter:on
public class KemApiDemo {

  public static void main(String[] args) throws Exception {
    // --- Receiver bootstraps long-term X25519 key pair (public key is shared with sender) ---
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519");
    KeyPair receiverKp = kpg.generateKeyPair();

    // --- Sender side: encapsulate to receiver's public key ---
    KEM kemSender = KEM.getInstance("DHKEM");                                    // GA in Java 21
    KEM.Encapsulator encapsulator = kemSender.newEncapsulator(receiverKp.getPublic());
    KEM.Encapsulated enc = encapsulator.encapsulate();                            // shared secret + KEM message

    SecretKey senderShared = enc.key();                                           // symmetric secret (not directly an AES key)
    byte[] kemMessage = enc.encapsulation();                                      // bytes to send to receiver

    // Derive AES key material from shared secret (demo: SHA-256 -> first 16 bytes = AES-128)
    SecretKey senderAesKey = deriveAesKey(senderShared);

    // Encrypt a message with AES/GCM
    byte[] iv = secureRandom(12);
    String message = "Confidential: KEM in Java 21 is neat.";
    byte[] plaintext = message.getBytes(StandardCharsets.UTF_8);
    byte[] ciphertext = aesGcmEncrypt(senderAesKey, iv, plaintext);

    System.out.println("Message to encrypt: " + message);
    // --- Transmit to receiver: kemMessage (+ optional params), iv, ciphertext ---
    // Display what you'd transmit
    System.out.println("KEM message (Base64): " + Base64.getEncoder().encodeToString(kemMessage));
    System.out.println("IV (Base64): " + Base64.getEncoder().encodeToString(iv));
    System.out.println("Ciphertext (Base64): " + Base64.getEncoder().encodeToString(ciphertext));


    // --- Receiver side: decapsulate using private key and reconstruct AES key ---
    KEM kemReceiver = KEM.getInstance("DHKEM");
    KEM.Decapsulator decapsulator = kemReceiver.newDecapsulator(receiverKp.getPrivate());
    SecretKey receiverShared = decapsulator.decapsulate(kemMessage);

    // Derive the same AES key
    SecretKey receiverAesKey = deriveAesKey(receiverShared);

    // Decrypt and verify
    byte[] recovered = aesGcmDecrypt(receiverAesKey, iv, ciphertext);
    System.out.println("Decrypted: " + new String(recovered, StandardCharsets.UTF_8));

    // Sanity: both sides derived identical key bytes
    System.out.println("Keys equal: " + Arrays.equals(
        senderAesKey.getEncoded(), receiverAesKey.getEncoded()));
  }

  // --- Symmetric crypto helpers (AES/GCM) ---

  private static byte[] aesGcmEncrypt(SecretKey key, byte[] iv, byte[] data) throws Exception {
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
    c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
    return c.doFinal(data);
  }

  private static byte[] aesGcmDecrypt(SecretKey key, byte[] iv, byte[] ct) throws Exception {
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
    c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
    return c.doFinal(ct);
  }

  // Demo KDF: SHA-256(sharedSecret) -> first 16 bytes for AES-128
  private static SecretKey deriveAesKey(SecretKey shared) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(shared.getEncoded());
    byte[] k = Arrays.copyOf(digest, 16);
    return new SecretKeySpec(k, "AES");
  }

  private static byte[] secureRandom(int n) {
    byte[] b = new byte[n];
    new SecureRandom().nextBytes(b);
    return b;
  }
}
