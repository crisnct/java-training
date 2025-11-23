package com.example.training;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AesDemo {

  public static void main(String[] a) throws Exception {
    KeyGenerator kg = KeyGenerator.getInstance("AES");
    kg.init(128);
    SecretKey k = kg.generateKey();
    Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
    c.init(Cipher.ENCRYPT_MODE, k);
    byte[] enc = c.doFinal("secret".getBytes("UTF-8"));
    c.init(Cipher.DECRYPT_MODE, k);
    System.out.println(new String(c.doFinal(enc), "UTF-8"));
  }

}
