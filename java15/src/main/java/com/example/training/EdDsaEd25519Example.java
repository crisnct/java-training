package com.example.training;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

//@formatter:off
/**
 * JEP 339 – Edwards-Curve Digital Signature Algorithm (EdDSA) Adds EdDSA (Ed25519 / Ed448) to java.security
 * as a standard signature algorithm, offering strong security and good performance.
 *
 * A digital signature (Ed25519, Ed448, RSA, etc.) is just a binary blob computed from:
 * - the message
 * - the private key
 * - the algorithm
 *
 * It is not an encrypted form of the message and it is not a serialized key.
 * Digital signatures are not encryption.
 * You’re not encrypting the message, you’re producing a signature over the message. Different primitives, different purpose.
 *
 * Signatures are arbitrary bytes.
 * Converting them directly to a UTF-8 String will corrupt the data.
 *
 * Scenarios where digital signature can be useful
 * - to sign jars
 * - to sign configuration file of the app which can not be modified
 * - to sign the payload received from server
 */
//@formatter:on
public class EdDsaEd25519Example {

  public static void main(String[] args) throws Exception {
    // 1. Generate an Ed25519 key pair
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519");
    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    String message = "Hello Cristian, this is Ed25519 on Java 15";
    byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

    // 2. Sign the message with the private key
    Signature signer = Signature.getInstance("Ed25519");
    signer.initSign(keyPair.getPrivate());
    signer.update(messageBytes);
    byte[] signature = signer.sign();
    String signatureBase64 = Base64.getEncoder().encodeToString(signature);
    System.out.println("Signature (Base64): " + signatureBase64);

    // 3. Verify the signature with the public key (OK case)
    Signature verifier = Signature.getInstance("Ed25519");
    verifier.initVerify(keyPair.getPublic());
    verifier.update(messageBytes);
    boolean valid = verifier.verify(signature);

    System.out.println("Original message: " + message);
    System.out.println("Signature valid (original): " + valid);

    // 4. Try to verify a tampered message (should fail)
    String tampered = "Hello Cristian, this is *not* the same message";
    byte[] tamperedBytes = tampered.getBytes(StandardCharsets.UTF_8);

    verifier.initVerify(keyPair.getPublic());
    verifier.update(tamperedBytes);
    boolean validTampered = verifier.verify(signature);

    System.out.println("Tampered message: " + tampered);
    System.out.println("Signature valid (tampered): " + validTampered);
  }
}
