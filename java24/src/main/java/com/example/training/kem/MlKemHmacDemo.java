package com.example.training.kem;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
import javax.crypto.KEM;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//@formatter:off
/**
 * MlKemHmacDemo (Java 24)
 *
 * Purpose:
 *  - Demonstrate post-quantum key establishment with ML-KEM (JEP 496) and use the derived secret
 *    immediately for a practical task: authenticating a message with HMAC-SHA256.
 *
 * Flow:
 *  1) Receiver generates an ML-KEM keypair (public key can be published).
 *  2) Sender encapsulates to receiver's public key -> gets shared SecretKey + encapsulation bytes.
 *  3) Receiver decapsulates using private key -> obtains the *same* SecretKey.
 *  4) Both sides use the shared secret as an HMAC key to authenticate application data.
 *
 * Why this matters:
 *  - ML-KEM is quantum-resistant key agreement. You can drop it into real systems to replace
 *    classical ECDH/RSA key exchanges for request signing, tokens, or session key derivation.
 */
//@formatter:on
public class MlKemHmacDemo {

  public static void main(String[] args) throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-KEM");
    kpg.initialize(new NamedParameterSpec("ML-KEM-768")); // balanced security/perf
    KeyPair receiverKP = kpg.generateKeyPair();

    KEM kem = KEM.getInstance("ML-KEM");
    KEM.Encapsulated enc = kem.newEncapsulator(receiverKP.getPublic()).encapsulate();
    SecretKey senderSecret = enc.key();
    byte[] kemCiphertext = enc.encapsulation(); // send to receiver

    // Use the shared secret as HMAC key (simple and standard)
    SecretKey senderMacKey = new SecretKeySpec(senderSecret.getEncoded(), "HmacSHA256");
    byte[] data = "purchase: id=42, amount=99.00".getBytes();
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(senderMacKey);
    byte[] tag = mac.doFinal(data);

    // Receiver derives the same secret
    SecretKey receiverSecret = kem.newDecapsulator(receiverKP.getPrivate()).decapsulate(kemCiphertext);
    SecretKey receiverMacKey = new SecretKeySpec(receiverSecret.getEncoded(), "HmacSHA256");
    Mac vrf = Mac.getInstance("HmacSHA256");
    vrf.init(receiverMacKey);
    byte[] tag2 = vrf.doFinal(data);

    System.out.println("HMAC OK: " + Arrays.equals(tag, tag2));
  }
}
