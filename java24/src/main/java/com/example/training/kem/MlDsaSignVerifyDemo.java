package com.example.training.kem;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.NamedParameterSpec;
import java.util.HexFormat;

//@formatter:off
/**
 * MlDsaSignVerifyDemo (Java 24)
 *
 * Purpose:
 *  - Demonstrate post-quantum **signatures** with ML-DSA (JEP 497): sign data and verify it.
 *
 * Typical uses:
 *  - Code signing, package signing, document signing, attestations where RSA/ECDSA are used today
 *    but you want quantum-resistant primitives.
 *
 * Notes:
 *  - Parameter sets: 44 / 65 / 87 (increasing security and signature size). We use 65.
 *  - API mirrors classic JCA usage: Signature.getInstance("ML-DSA"), initSign/initVerify, sign/verify.
 */
//@formatter:on
public class MlDsaSignVerifyDemo {

  public static void main(String[] args) throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-DSA");
    kpg.initialize(new NamedParameterSpec("ML-DSA-65")); // balanced profile
    KeyPair kp = kpg.generateKeyPair();

    byte[] message = "release-artifact-sha256=deadbeef...".getBytes();

    // Sign
    Signature signer = Signature.getInstance("ML-DSA");
    signer.initSign(kp.getPrivate());
    signer.update(message);
    byte[] sig = signer.sign();

    // Verify
    Signature verifier = Signature.getInstance("ML-DSA");
    verifier.initVerify(kp.getPublic());
    verifier.update(message);
    boolean ok = verifier.verify(sig);

    System.out.println("Signature valid: " + ok);
    System.out.println("Signature (hex): " + HexFormat.of().formatHex(sig));
  }
}
